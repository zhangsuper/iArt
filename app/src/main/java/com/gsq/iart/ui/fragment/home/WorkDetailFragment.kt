package com.gsq.iart.ui.fragment.home

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.init
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.data.Constant
import com.gsq.iart.data.Constant.COMPLEX_TYPE_COLLECT
import com.gsq.iart.data.Constant.COMPLEX_TYPE_GROUP
import com.gsq.iart.data.Constant.DATA_WORK
import com.gsq.iart.data.Constant.DOWNLOAD_PARENT_PATH
import com.gsq.iart.data.bean.DetailArgsType
import com.gsq.iart.data.bean.WorksBean
import com.gsq.iart.data.event.BigImageClickEvent
import com.gsq.iart.databinding.FragmentWorkDetailBinding
import com.gsq.iart.viewmodel.WorksViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.visible
import kotlinx.android.synthetic.main.fragment_work_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


/**
 * 作品详情
 */
class WorkDetailFragment : BaseFragment<WorksViewModel, FragmentWorkDetailBinding>() {

    private var worksBean: WorksBean? = null
    private var intentType: String? = null
    private var RC_EXTERNAL_STORAGE_CODE: Int = 10

    private lateinit var fragmentList: ArrayList<Fragment>
    private var currentSelectedIndex = 0


    override fun initView(savedInstanceState: Bundle?) {
        worksBean = arguments?.getSerializable(DATA_WORK) as? WorksBean
        intentType = arguments?.getString(Constant.INTENT_TYPE, COMPLEX_TYPE_GROUP)
        fragmentList = arrayListOf()
        view_pager.init(this, fragmentList)
        view_pager.offscreenPageLimit = 6
        initListener()
        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                currentSelectedIndex = position
                tv_index.text = "${position + 1}/${fragmentList.size}"
            }
        })
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()
        StatusBarUtil.init(
            requireActivity(),
            fitSystem = false,
            statusBarColor = R.color.transparent,
            isDarkFont = false
        )
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        worksBean?.let {
            if (intentType == COMPLEX_TYPE_COLLECT) {
                mViewModel.getWorkDetail(it.workId)
            } else {
                mViewModel.getWorkDetail(it.id)
            }

        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.workDetailDataState.observe(viewLifecycleOwner, Observer {
            if (it.isSuccess) {
                worksBean = it.data
                worksBean?.displayType//展示方式：1=直接展示；2=横向拼接
                LogUtils.d("worksBean.displayType:${worksBean?.displayType}")
                updateCollectState()
                if (worksBean?.displayType == 2) {
                    ToastUtils.showLong("横向拼接")
                    var hdPics = it.data?.hdPics
                    if (hdPics != null) {
                        for (picbean in hdPics) {
                            fragmentList.add(PreviewImageFragment.start(DetailArgsType(picbean)))
                        }
                        view_pager.adapter?.notifyDataSetChanged()
                        if (fragmentList.size == 1) {
                            tv_index.gone()
                        } else {
                            tv_index.visible()
                        }
                    }
                    tv_index.text = "1/${fragmentList.size}"
                } else {
//                    if(worksBean?.workType == "COLL"){
//                        //合集
//                    }else{
//
//                    }
                    var hdPics = it.data?.hdPics
                    if (hdPics != null) {
                        for (picbean in hdPics) {
                            fragmentList.add(PreviewImageFragment.start(DetailArgsType(picbean)))
                        }
                        view_pager.adapter?.notifyDataSetChanged()
                        if (fragmentList.size == 1) {
                            tv_index.gone()
                        } else {
                            tv_index.visible()
                        }
                    }
                    tv_index.text = "1/${fragmentList.size}"
                }
            } else {
                ToastUtils.showLong(it.errorMsg)
            }
        })
        mViewModel.updateCollectDataState.observe(viewLifecycleOwner) {
            if (it.isSuccess) {
                worksBean?.isCollect = if (worksBean?.isCollect == 0) 1 else 0
                updateCollectState()
                when (worksBean?.isCollect) {
                    1 -> {
                        ToastUtils.showLong("收藏成功")
                    }
                    else -> {
                        ToastUtils.showLong("已取消收藏")
                    }
                }
            } else {
                it.errorMsg?.let {
                    ToastUtils.showLong(it)
                }
            }
        }
    }

    private fun initListener() {
        iv_back.setOnClickListener {
            nav().navigateUp()
        }
        iv_introduce.setOnClickListener {
            //简介
            var args = Bundle()
            args.putSerializable(DATA_WORK, worksBean)
            nav().navigateAction(R.id.action_workDetailFragment_to_workIntroduceFragment, args)
        }
        iv_collect.setOnClickListener {
            //收藏与取消收藏
            if (CacheUtil.isLogin()) {
                worksBean?.let {
                    when (it.isCollect) {
                        1 -> {
                            mViewModel.collectRemoveWork(it.id)
                        }
                        else -> {
                            mViewModel.collectAddWork(it.id)
                        }
                    }

                }
            } else {
                //跳转登录界面
                nav().navigateAction(R.id.action_mainFragment_to_loginFragment)
            }

        }
        iv_download.setOnClickListener {
            if (CacheUtil.isLogin()) {
                if (CacheUtil.getUser()?.memberType == 1) {
                    //下载
                    checkStoragePermission()
                } else {
                    //跳转到会员页
                    nav().navigateAction(R.id.action_workDetailFragment_to_memberFragment)
                }
            } else {
                //跳转登录界面
                nav().navigateAction(R.id.action_mainFragment_to_loginFragment)
            }
        }
        iv_scale.setOnClickListener {
//            (fragmentList[currentSelectedIndex] as PreviewImageFragment).getPhotoView().maximumScale
//                .setScale(100f, true)
        }
        iv_rota.setOnClickListener {
            (fragmentList[currentSelectedIndex] as PreviewImageFragment).getPhotoView().rotation -= 90
        }
    }

    @AfterPermissionGranted(10)
    private fun checkStoragePermission() {
        if (EasyPermissions.hasPermissions(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            // Already have permission, do the thing
            var url = worksBean?.hdPics?.get(view_pager.currentItem)?.url
            url?.let {
                FileUtils.createOrExistsDir(DOWNLOAD_PARENT_PATH)
                startDownload(
                    it,
                    Constant.DOWNLOAD_PARENT_PATH,
                    "art_${System.currentTimeMillis()}.jpg"
                )
                worksBean?.id?.let {
                    mViewModel.downloadInc(it)
                }
            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                requireActivity(),
                "应用程序需要您的存储权限",
                RC_EXTERNAL_STORAGE_CODE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun startDownload(url: String, dirPath: String, fileName: String) {
        val downloadId = PRDownloader.download(url, dirPath, fileName)
            .build()
            .setOnStartOrResumeListener {
                progress_bar.visible()
            }
            .setOnPauseListener { }
            .setOnCancelListener {
                progress_bar.gone()
            }
            .setOnProgressListener {
                progress_bar.progress = (it.currentBytes * 100 / it.totalBytes).toInt()
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    progress_bar.gone()
                    ToastUtils.showLong("下载成功")
                }

                override fun onError(error: com.downloader.Error?) {
                    progress_bar.gone()
                    ToastUtils.showLong("下载失败！")
                }
            })
    }

    private fun updateCollectState() {
        when (worksBean?.isCollect) {
            1 -> {
                iv_collect.setImageResource(R.drawable.icon_shoucang_grey)
            }
            else -> {
                iv_collect.setImageResource(R.drawable.icon_shoucang)
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_EXTERNAL_STORAGE_CODE) {
            checkStoragePermission()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: BigImageClickEvent?) {
        event?.let {
            if (content_view.isVisible) {
                content_view.gone()
            } else {
                content_view.visible()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}