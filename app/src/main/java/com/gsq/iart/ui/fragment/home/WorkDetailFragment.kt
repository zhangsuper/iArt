package com.gsq.iart.ui.fragment.home

import android.os.Bundle
import com.airbnb.mvrx.asMavericksArgs
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.data.bean.ArgsType
import com.gsq.iart.databinding.FragmentWorkDetailBinding
import com.gsq.iart.viewmodel.WorksViewModel

/**
 * 作品详情
 */
class WorkDetailFragment: BaseFragment<WorksViewModel, FragmentWorkDetailBinding>() {

    companion object {
        fun start(args: ArgsType): WorkDetailFragment {
            val fragment = WorkDetailFragment()
            fragment.arguments = args.asMavericksArgs()
            return fragment
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
    }
}