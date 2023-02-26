package com.gsq.iart.ui.fragment.home

import android.os.Bundle
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.init
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.data.Constant
import com.gsq.iart.data.bean.WorksBean
import com.gsq.iart.databinding.FragmentWorkIntroduceBinding
import com.gsq.iart.ui.adapter.TagAdapter
import com.gsq.iart.viewmodel.WorksViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.visible
import kotlinx.android.synthetic.main.fragment_work_introduce.*

/**
 * 作品简介
 */
class WorkIntroduceFragment : BaseFragment<WorksViewModel, FragmentWorkIntroduceBinding>() {

    private var worksBean: WorksBean? = null
    private val tagAdapter: TagAdapter by lazy { TagAdapter(arrayListOf()) }

    override fun initView(savedInstanceState: Bundle?) {
        worksBean = arguments?.getSerializable(Constant.DATA_WORK) as? WorksBean

        close_btn.setOnClickListener {
            nav().navigateUp()
        }

        StatusBarUtil.init(requireActivity(), statusBarColor = R.color.white)

        val layoutManager = FlexboxLayoutManager(context)
        //方向 主轴为水平方向，起点在左端
        layoutManager.flexDirection = FlexDirection.ROW
        //左对齐
        layoutManager.justifyContent = JustifyContent.FLEX_START
        tag_recycler_view.init(layoutManager, tagAdapter, false)
    }

    override fun initData() {
        tv_title.text = worksBean?.name
        tv_author_dynasty.text =
            "${worksBean?.author}  |  ${worksBean?.age}"
        if (worksBean?.size?.isNotEmpty() == true) {
            tv_author_dynasty.text =
                "${worksBean?.author}  |  ${worksBean?.age}".plus("  |  ${worksBean?.size}cm")
        }
        if (worksBean?.owner.isNullOrEmpty()) {
            layout_scf.gone()
        } else {
            layout_scf.visible()
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
            layout_ys.visible()
            tv_ys.text = worksBean?.materialType
        }
        if (worksBean?.skilOfPainting.isNullOrEmpty()) {
            layout_bxjf.gone()
        } else {
            layout_bxjf.visible()
            tv_bxjf.text = worksBean?.skilOfPainting
        }
        if (worksBean?.mediaType.isNullOrEmpty()) {
            layout_cz.gone()
        } else {
            layout_cz.visible()
            tv_cz.text = worksBean?.mediaType
        }
        if (worksBean?.styleType.isNullOrEmpty()) {
            layout_xz.gone()
        } else {
            layout_xz.visible()
            tv_xz.text = worksBean?.styleType
        }
        if (worksBean?.tags.isNullOrEmpty()) {
            layout_bq.gone()
        } else {
//            tv_bq.text = worksBean?.tags
            layout_bq.visible()
            var tagList = worksBean?.tags?.split(",")
            tagAdapter.setList(tagList)
        }
        if (worksBean?.description.isNullOrEmpty()) {
            layout_jj.gone()
        } else {
            layout_jj.visible()
            tv_jj.text = worksBean?.description
        }
        if (worksBean?.referenceBook.isNullOrEmpty()) {
            layout_zlsj.gone()
        } else {
            layout_zlsj.visible()
            tv_zlsj.text = worksBean?.referenceBook
        }
    }

    override fun createObserver() {
        super.createObserver()
    }
}