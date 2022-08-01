package com.gsq.iart.ui.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.init
import com.gsq.iart.data.Constant.DATA_WORK
import com.gsq.iart.data.bean.DetailArgsType
import com.gsq.iart.data.bean.WorksBean
import com.gsq.iart.databinding.FragmentWorkDetailBinding
import com.gsq.iart.viewmodel.WorksViewModel
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
                var hdPics = it.data?.hdPics
                if (hdPics != null) {
                    for (picbean in hdPics){
                        fragmentList.add(PreviewImageFragment.start(DetailArgsType(picbean)))
                    }
                    view_pager.adapter?.notifyDataSetChanged()
                }
            }else{
                ToastUtils.showLong(it.errorMsg)
            }
        })
    }
}