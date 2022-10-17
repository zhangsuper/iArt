package com.gsq.iart.ui.fragment.home

import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.init
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.data.Constant
import com.gsq.iart.data.Constant.COMPLEX_TYPE_COLLECT
import com.gsq.iart.data.Constant.COMPLEX_TYPE_GROUP
import com.gsq.iart.data.Constant.DATA_WORK
import com.gsq.iart.data.bean.DetailArgsType
import com.gsq.iart.data.bean.WorksBean
import com.gsq.iart.databinding.FragmentWorkDetailBinding
import com.gsq.iart.viewmodel.WorksViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.visible
import com.gsq.mvvm.network.NetworkUtil.url
import com.liulishuo.okdownload.DownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import kotlinx.android.synthetic.main.fragment_work_detail.*
import java.io.File
import java.lang.Exception


/**
 * 作品详情
 */
class WorkDetailFragment : BaseFragment<WorksViewModel, FragmentWorkDetailBinding>() {

    private var worksBean: WorksBean? = null
    private var intentType: String? = null

    private lateinit var fragmentList: ArrayList<Fragment>

    override fun initView(savedInstanceState: Bundle?) {
        worksBean = arguments?.getSerializable(DATA_WORK) as? WorksBean
        intentType = arguments?.getString(Constant.INTENT_TYPE, COMPLEX_TYPE_GROUP)
        fragmentList = arrayListOf()
        view_pager.init(this, fragmentList)
        initListener()
        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                tv_index.text = "${position + 1}/${fragmentList.size}"
            }
        })
        var gestureDetector = GestureDetector(gesturelistener())
        view_pager.setOnTouchListener { view, event ->
            var flage = 0
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    // 记录点下去的点（起点）
//                    flage =0
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    // 记录移动后的点（终点）
//                    flage = 1
//                }
//                MotionEvent.ACTION_UP -> {
//                    if(flage == 0){
//                        if(view_pager.isVisible){
//                            view_pager.gone()
//                        }else{
//                            view_pager.visible()
//                        }
//                    }
//                }
//            }
            return@setOnTouchListener gestureDetector.onTouchEvent(event)
        }

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
            //下载
            var url = worksBean?.hdPics?.get(view_pager.currentItem)?.url
            url?.let {
                createDownloadTask(it,System.currentTimeMillis().toString().plus(".jpg")).enqueue(object: DownloadListener{
                    override fun taskStart(task: DownloadTask) {
                    }

                    override fun connectTrialStart(
                        task: DownloadTask,
                        requestHeaderFields: MutableMap<String, MutableList<String>>
                    ) {
                    }

                    override fun connectTrialEnd(
                        task: DownloadTask,
                        responseCode: Int,
                        responseHeaderFields: MutableMap<String, MutableList<String>>
                    ) {
                    }

                    override fun downloadFromBeginning(
                        task: DownloadTask,
                        info: BreakpointInfo,
                        cause: ResumeFailedCause
                    ) {
                        progress_bar.visible()
                    }

                    override fun downloadFromBreakpoint(task: DownloadTask, info: BreakpointInfo) {
                    }

                    override fun connectStart(
                        task: DownloadTask,
                        blockIndex: Int,
                        requestHeaderFields: MutableMap<String, MutableList<String>>
                    ) {
                    }

                    override fun connectEnd(
                        task: DownloadTask,
                        blockIndex: Int,
                        responseCode: Int,
                        responseHeaderFields: MutableMap<String, MutableList<String>>
                    ) {
                    }

                    override fun fetchStart(
                        task: DownloadTask,
                        blockIndex: Int,
                        contentLength: Long
                    ) {
                    }

                    override fun fetchProgress(
                        task: DownloadTask,
                        blockIndex: Int,
                        increaseBytes: Long
                    ) {
                        progress_bar.progress = blockIndex
                    }

                    override fun fetchEnd(
                        task: DownloadTask,
                        blockIndex: Int,
                        contentLength: Long
                    ) {

                    }

                    override fun taskEnd(
                        task: DownloadTask,
                        cause: EndCause,
                        realCause: Exception?
                    ) {
                        progress_bar.gone()
                    }
                })
            }

        }
    }

    private fun createDownloadTask(url: String, fileName: String): DownloadTask {
        return DownloadTask.Builder(url, File(Constant.DOWNLOAD_PARENT_PATH)) //设置下载地址和下载目录，这两个是必须的参数
            .setFilename(fileName) //设置下载文件名，没提供的话先看 response header ，再看 url path(即启用下面那项配置)
            .setFilenameFromResponse(false) //是否使用 response header or url path 作为文件名，此时会忽略指定的文件名，默认false
            .setPassIfAlreadyCompleted(true) //如果文件已经下载完成，再次下载时，是否忽略下载，默认为true(忽略)，设为false会从头下载
            .setConnectionCount(1) //需要用几个线程来下载文件，默认根据文件大小确定；如果文件已经 split block，则设置后无效
            .setPreAllocateLength(false) //在获取资源长度后，设置是否需要为文件预分配长度，默认false
            .setMinIntervalMillisCallbackProcess(100) //通知调用者的频率，避免anr，默认3000
            .setWifiRequired(false) //是否只允许wifi下载，默认为false
            .setAutoCallbackToUIThread(true) //是否在主线程通知调用者，默认为true
            //.setHeaderMapFields(new HashMap<String, List<String>>())//设置请求头
            //.addHeader(String key, String value)//追加请求头
            .setPriority(0) //设置优先级，默认值是0，值越大下载优先级越高
            .setReadBufferSize(4096) //设置读取缓存区大小，默认4096
            .setFlushBufferSize(16384) //设置写入缓存区大小，默认16384
            .setSyncBufferSize(65536) //写入到文件的缓冲区大小，默认65536
            .setSyncBufferIntervalMillis(2000) //写入文件的最小时间间隔，默认2000
            .build()
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

    class gesturelistener : GestureDetector.OnGestureListener {
        override fun onDown(p0: MotionEvent?): Boolean {
            Log.d("tag", "onDown")
            return false
        }

        override fun onShowPress(p0: MotionEvent?) {
            Log.d("tag", "onShowPress")
        }

        override fun onSingleTapUp(p0: MotionEvent?): Boolean {
            Log.d("tag", "onSingleTapUp")
            return false
        }

        override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
            Log.d("tag", "onScroll")
            return false
        }

        override fun onLongPress(p0: MotionEvent?) {
            Log.d("tag", "onLongPress")
        }

        override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
            Log.d("tag", "onFling")
            return false
        }

    }
}