package com.gsq.iart.ui.fragment.home

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.*
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.gsq.iart.BuildConfig
import com.gsq.iart.R
import com.gsq.iart.app.App
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.init
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.MobAgentUtil
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.data.Constant
import com.gsq.iart.data.Constant.COMPLEX_TYPE_COLLECT
import com.gsq.iart.data.Constant.COMPLEX_TYPE_DICTIONARY
import com.gsq.iart.data.Constant.COMPLEX_TYPE_GROUP
import com.gsq.iart.data.Constant.DATA_WORK
import com.gsq.iart.data.Constant.DOWNLOAD_PARENT_PATH
import com.gsq.iart.data.bean.DetailArgsType
import com.gsq.iart.data.bean.DictionaryWorksBean
import com.gsq.iart.data.bean.WorkHdPics
import com.gsq.iart.data.bean.WorksBean
import com.gsq.iart.data.event.BigImageClickEvent
import com.gsq.iart.databinding.FragmentWorkDetailBinding
import com.gsq.iart.ui.fragment.mine.MemberFragment
import com.gsq.iart.viewmodel.WorksViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.onClick
import com.gsq.mvvm.ext.view.visible
import com.gsq.mvvm.util.SaveUtils
import kotlinx.android.synthetic.main.fragment_work_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


/**
 * 作品详情
 */
class WorkDetailFragment : BaseFragment<WorksViewModel, FragmentWorkDetailBinding>() {
    companion object {
        var TAG = "WorkDetailFragment"
    }

    private var worksBean: WorksBean? = null
    private var dictionaryWorksBean: DictionaryWorksBean? = null
    private var intentType: String? = null
    private var RC_EXTERNAL_STORAGE_CODE: Int = 10

    private lateinit var fragmentList: ArrayList<Fragment>
    private var currentSelectedIndex = 0


    override fun initView(savedInstanceState: Bundle?) {
        intentType = arguments?.getString(Constant.INTENT_TYPE, COMPLEX_TYPE_GROUP)

        if (intentType == COMPLEX_TYPE_DICTIONARY) {
            //图典
            dictionaryWorksBean = arguments?.getSerializable(DATA_WORK) as? DictionaryWorksBean
            common_title_layout.gone()
            dictionary_title_layout.visible()
            work_name.text = dictionaryWorksBean?.name
            work_source.text =
                "来源：[${dictionaryWorksBean?.mainAge}]${dictionaryWorksBean?.mainName}"
            contrast_view.visible()

            var eventMap = mutableMapOf<String, Any?>()
            eventMap["work_id"] = dictionaryWorksBean?.id
            MobAgentUtil.onEvent("preview_jump", eventMap)
        } else {
            worksBean = arguments?.getSerializable(DATA_WORK) as? WorksBean
            common_title_layout.visible()
            dictionary_title_layout.gone()
            contrast_view.gone()

            var eventMap = mutableMapOf<String, Any?>()
            eventMap["work_id"] = worksBean?.id
            MobAgentUtil.onEvent("preview_jump", eventMap)
        }

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
        if (intentType == COMPLEX_TYPE_DICTIONARY) {
            loadDictionaryData()
        } else {
            worksBean?.let {
                if (intentType == COMPLEX_TYPE_COLLECT) {
                    mViewModel.getWorkDetail(it.workId)
                } else {
                    mViewModel.getWorkDetail(it.id)
                }

            }
        }
    }

    private fun loadDictionaryData() {
        var workHdPics = WorkHdPics("", dictionaryWorksBean?.image ?: "")
        fragmentList.add(PreviewImageFragment.start(DetailArgsType(workHdPics)))
        view_pager.adapter?.notifyDataSetChanged()
        if (fragmentList.size == 1) {
            tv_index.gone()
        } else {
            tv_index.visible()
        }
        tv_index.text = "1/${fragmentList.size}"

        updateCompareStatus()
        iv_contrast.onClick {
            dictionaryWorksBean?.let {
                if (it.isAddCompare) {
                    //移除对比列表
                    CacheUtil.removeCompare(it)
                    ToastUtils.showShort("移除成功")
                } else {
                    //加入对比列表
                    CacheUtil.addCompareList(it)
                    ToastUtils.showShort("加入成功")
                }
                it.isAddCompare = !it.isAddCompare
                updateCompareStatus()
            }
        }
    }

    private fun updateCompareStatus() {
        if (dictionaryWorksBean?.isAddCompare == true) {
            iv_contrast.setImageResource(R.drawable.compare_remove)
        } else {
            iv_contrast.setImageResource(R.drawable.icon_add)
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.workDetailDataState.observe(viewLifecycleOwner, Observer {
            if (it.isSuccess) {
                worksBean = it.data
                LogUtils.d("worksBean.displayType:${worksBean?.displayType}")
                updateCollectState()
                if (worksBean?.displayType == 2) {//展示方式：1=直接展示；2=横向拼接
//                    ToastUtils.showLong("横向拼接")
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

                var eventMap = mutableMapOf<String, Any?>()
                eventMap["work_id"] = worksBean?.id
                MobAgentUtil.onEvent("preview_show", eventMap)
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
        iv_back.onClick {
            nav().navigateUp()
        }
        iv_dictionary_back.onClick {
            nav().navigateUp()
        }
        iv_introduce.onClick {
            //简介
            var eventMap = mutableMapOf<String, Any?>()
            eventMap["work_id"] = worksBean?.id
            MobAgentUtil.onEvent("introduce", eventMap)

            var args = Bundle()
            args.putSerializable(DATA_WORK, worksBean)
            nav().navigateAction(R.id.action_workDetailFragment_to_workIntroduceFragment, args)
        }
        iv_collect.onClick {
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

                    var eventMap = mutableMapOf<String, Any?>()
                    eventMap["work_id"] = worksBean?.id
                    MobAgentUtil.onEvent("collect", eventMap)
                }
            } else {
                //跳转登录界面
                var eventMap = mutableMapOf<String, Any?>()
                eventMap["type"] = "collect"
                eventMap["work_id"] = worksBean?.id
                MobAgentUtil.onEvent("signin", eventMap)
                nav().navigateAction(R.id.action_mainFragment_to_loginFragment)
            }

        }
        iv_download.onClick {
            if (CacheUtil.isLogin()) {
                if (CacheUtil.getUserVipStatus() != 0 || BuildConfig.DEBUG) {
                    //下载
                    if (intentType == COMPLEX_TYPE_DICTIONARY) {
                        var eventMap = mutableMapOf<String, Any?>()
                        eventMap["work_id"] = dictionaryWorksBean?.id
                        MobAgentUtil.onEvent("download", eventMap)
                    } else {
                        var eventMap = mutableMapOf<String, Any?>()
                        eventMap["work_id"] = worksBean?.id
                        MobAgentUtil.onEvent("download", eventMap)
                    }

                    checkStoragePermission()
                } else {
                    //跳转到会员页
                    nav().navigateAction(
                        R.id.action_workDetailFragment_to_memberFragment,
                        bundleOf(MemberFragment.INTENT_KEY_TYPE to MemberFragment.INTENT_VALUE_DOWNLOAD)
                    )
                }
            } else {
                //跳转登录界面
                nav().navigateAction(R.id.action_mainFragment_to_loginFragment)
            }
        }
        iv_scale.setOnClickListener {
            (fragmentList[currentSelectedIndex] as PreviewImageFragment).getPhotoView()
                .setScaleAndCenter(100f, null)
//            (fragmentList[currentSelectedIndex] as PreviewImageFragment).getPhotoView().maximumScale
//                .setScale(100f, true)
        }
        iv_rota.setOnClickListener {
//            (fragmentList[currentSelectedIndex] as PreviewImageFragment).getPhotoView().rotation -= 90
            var eventMap = mutableMapOf<String, Any?>()
            eventMap["work_id"] = worksBean?.id
            MobAgentUtil.onEvent("rotate", eventMap)
            var orientation =
                (fragmentList[currentSelectedIndex] as PreviewImageFragment).getPhotoView().orientation
            when (orientation) {
                ORIENTATION_0 -> {
                    (fragmentList[currentSelectedIndex] as PreviewImageFragment).getPhotoView().orientation =
                        ORIENTATION_270
                }
                ORIENTATION_90 -> {
                    (fragmentList[currentSelectedIndex] as PreviewImageFragment).getPhotoView().orientation =
                        ORIENTATION_0
                }
                ORIENTATION_180 -> {
                    (fragmentList[currentSelectedIndex] as PreviewImageFragment).getPhotoView().orientation =
                        ORIENTATION_90
                }
                ORIENTATION_270 -> {
                    (fragmentList[currentSelectedIndex] as PreviewImageFragment).getPhotoView().orientation =
                        ORIENTATION_180
                }
            }
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
            if (intentType == COMPLEX_TYPE_DICTIONARY) {//图典
                var url = dictionaryWorksBean?.image
                url?.let {
                    dictionaryWorksBean?.id?.let {
                        mViewModel.downloadInc(it.toString())
                    }
                    var fileName = it.substring(
                        it.lastIndexOf('/') + 1,
                        it.indexOf('?')
                    )
                    LogUtils.dTag(TAG, "testname:${fileName}")

                    FileUtils.createOrExistsDir(DOWNLOAD_PARENT_PATH)
                    var imageResource = DOWNLOAD_PARENT_PATH + File.separator + fileName
                    if (FileUtils.isFileExists(imageResource)) {
                        SaveUtils.saveImgFileToAlbum(App.instance, imageResource)
                        ToastUtils.showLong("文件已保存在相册")
                        return@let
                    }
                    LogUtils.dTag(TAG, "startDownload")
                    startDownload(
                        it,
                        DOWNLOAD_PARENT_PATH,
                        fileName
                    )
                }
            } else {
                var url = worksBean?.hdPics?.get(view_pager.currentItem)?.url
                url?.let {
                    worksBean?.id?.let {
                        mViewModel.downloadInc(it)
                    }
                    var fileName = it.substring(
                        it.lastIndexOf('/') + 1,
                        it.indexOf('?')
                    )
                    LogUtils.dTag(TAG, "testname:${fileName}")
//                var imageResource = FileUtil.getSDPath(App.instance) + fileName

                    FileUtils.createOrExistsDir(DOWNLOAD_PARENT_PATH)
                    var imageResource = DOWNLOAD_PARENT_PATH + File.separator + fileName
                    if (FileUtils.isFileExists(imageResource)) {
                        SaveUtils.saveImgFileToAlbum(App.instance, imageResource)
                        ToastUtils.showLong("文件已保存在相册")
                        return@let
                    }
                    LogUtils.dTag(TAG, "startDownload")
                    startDownload(
                        it,
                        DOWNLOAD_PARENT_PATH,
                        fileName
                    )
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
                    var destPath = DOWNLOAD_PARENT_PATH + File.separator + fileName

                    //把图片保存后声明这个广播事件通知系统相册有新图片到来
//                    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
//                    val uri: Uri = Uri.fromFile(File(destPath))
//                    intent.data = uri
//                    App.instance.sendBroadcast(intent)

                    SaveUtils.saveImgFileToAlbum(App.instance, destPath)
                    ToastUtils.showLong("文件已保存在相册")
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