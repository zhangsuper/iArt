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
import com.gsq.mvvm.ext.view.gone
import kotlinx.android.synthetic.main.fragment_work_introduce.*

/**
 * 作品简介
 */
class WorkIntroduceFragment : BaseFragment<WorksViewModel, FragmentWorkIntroduceBinding>() {

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
        tv_author_dynasty.text =
            "${worksBean?.author}  |  ${worksBean?.age}  |  ${worksBean?.size}cm"
        if (worksBean?.owner.isNullOrEmpty()) {
            layout_scf.gone()
        } else {
            tv_scf.text = worksBean?.owner
        }
        if (worksBean?.detailedSubject.isNullOrEmpty()) {
            if (worksBean?.subject.isNullOrEmpty()) {
                layout_tc.gone()
            } else {
                tv_tc.text = worksBean?.subject
            }
        } else {
            tv_tc.text = worksBean?.subject + "-" + worksBean?.detailedSubject
        }
        if (worksBean?.materialType.isNullOrEmpty()) {
            layout_ys.gone()
        } else {
            tv_ys.text = worksBean?.materialType
        }
        if (worksBean?.skilOfPainting.isNullOrEmpty()) {
            layout_bxjf.gone()
        } else {
            tv_bxjf.text = worksBean?.skilOfPainting
        }
        if (worksBean?.mediaType.isNullOrEmpty()) {
            layout_cz.gone()
        } else {
            tv_cz.text = worksBean?.mediaType
        }
        if (worksBean?.styleType.isNullOrEmpty()) {
            layout_xz.gone()
        } else {
            tv_xz.text = worksBean?.styleType
        }
        if (worksBean?.tags.isNullOrEmpty()) {
            layout_bq.gone()
        } else {
            tv_bq.text = worksBean?.tags
        }
        if (worksBean?.description.isNullOrEmpty()) {
            layout_jj.gone()
        } else {
            tv_jj.text = worksBean?.description
        }
        if (worksBean?.referenceBook.isNullOrEmpty()) {
            layout_zlsj.gone()
        } else {
            tv_zlsj.text = worksBean?.referenceBook
        }
    }

    override fun createObserver() {
        super.createObserver()
    }
}