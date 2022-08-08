package com.gsq.iart.ui.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
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
class WorkDetailFragment: BaseFragment<WorksViewModel, FragmentWorkDetailBinding>() {

    private var worksBean: WorksBean? = null

    private lateinit var fragmentList: ArrayList<Fragment>

    override fun initView(savedInstanceState: Bundle?) {
        worksBean = arguments?.getSerializable(DATA_WORK) as? WorksBean
        fragmentList = arrayListOf()
        view_pager.init(this,fragmentList)
        iv_back.setOnClickListener {
            nav().navigateUp()
        }
        iv_introduce.setOnClickListener {
            //简介
            var args = Bundle()
            args.putSerializable(DATA_WORK, worksBean)
            nav().navigateAction(R.id.action_workDetailFragment_to_workIntroduceFragment,args)
        }
        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){

            override fun onPageSelected(position: Int) {
                tv_index.text = "${position+1}/${fragmentList.size}"
            }
        })
    }

    override fun onResume() {
        super.onResume()
        StatusBarUtil.init(requireActivity(), fitSystem = false,statusBarColor = R.color.transparent,isDarkFont =false)
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
                var hdPics = it.data?.hdPics
                if (hdPics != null) {
                    for (picbean in hdPics){
                        fragmentList.add(PreviewImageFragment.start(DetailArgsType(picbean)))
                    }
                    view_pager.adapter?.notifyDataSetChanged()
                    if(fragmentList.size == 1){
                        tv_index.gone()
                    }else{
                        tv_index.visible()
                    }
                }
            }else{
                ToastUtils.showLong(it.errorMsg)
            }
        })
    }
}