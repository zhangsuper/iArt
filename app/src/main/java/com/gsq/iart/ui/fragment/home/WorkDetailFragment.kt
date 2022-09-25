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
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.data.Constant.DATA_WORK
import com.gsq.iart.data.bean.DetailArgsType
import com.gsq.iart.data.bean.WorksBean
import com.gsq.iart.databinding.FragmentWorkDetailBinding
import com.gsq.iart.viewmodel.WorksViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.visible
import kotlinx.android.synthetic.main.fragment_work_detail.*

/**
 * 作品详情
 */
class WorkDetailFragment : BaseFragment<WorksViewModel, FragmentWorkDetailBinding>() {

    private var worksBean: WorksBean? = null

    private lateinit var fragmentList: ArrayList<Fragment>

    override fun initView(savedInstanceState: Bundle?) {
        worksBean = arguments?.getSerializable(DATA_WORK) as? WorksBean
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
            mViewModel.getWorkDetail(it.id)
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
                worksBean?.isCollect = worksBean?.isCollect != true
                updateCollectState()
                when (worksBean?.isCollect) {
                    true -> {
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
            worksBean?.let {
                when (it.isCollect) {
                    true -> {
                        mViewModel.collectRemoveWork(it.id)
                    }
                    else -> {
                        mViewModel.collectAddWork(it.id)
                    }
                }

            }
        }
    }

    private fun updateCollectState() {
        when (worksBean?.isCollect) {
            true -> {
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