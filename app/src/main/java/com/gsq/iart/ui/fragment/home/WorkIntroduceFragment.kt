package com.gsq.iart.ui.fragment.home

import android.os.Bundle
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.data.Constant
import com.gsq.iart.data.bean.WorksBean
import com.gsq.iart.databinding.FragmentWorkIntroduceBinding
import com.gsq.iart.viewmodel.WorksViewModel
import com.gsq.mvvm.ext.nav
import kotlinx.android.synthetic.main.fragment_work_introduce.*

/**
 * 作品简介
 */
class WorkIntroduceFragment: BaseFragment<WorksViewModel, FragmentWorkIntroduceBinding>() {

    private var worksBean: WorksBean? = null

    override fun initView(savedInstanceState: Bundle?) {
        worksBean = arguments?.getSerializable(Constant.DATA_WORK) as? WorksBean

        close_btn.setOnClickListener {
            nav().navigateUp()
        }

        StatusBarUtil.init(requireActivity(), statusBarColor = R.color.white)
    }

    override fun initData() {
        tv_title.text = worksBean?.name
        tv_author_dynasty.text = "${worksBean?.author} | ${worksBean?.age} | ${worksBean?.size}"
        tv_scf.text = worksBean?.collector
        tv_tc.text = worksBean?.subject
        tv_xz.text = worksBean?.styleType
        tv_jj.text = worksBean?.description
        tv_cz.text = worksBean?.mediaType
        tv_jsf.text = worksBean?.otherPostscriptInfo
    }

    override fun createObserver() {
        super.createObserver()
    }
}