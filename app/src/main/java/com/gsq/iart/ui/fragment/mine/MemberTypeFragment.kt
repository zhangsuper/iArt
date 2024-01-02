package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import com.airbnb.mvrx.args
import com.airbnb.mvrx.asMavericksArgs
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.data.bean.MemberArgsType
import com.gsq.iart.databinding.FragmentMemberTypeBinding
import com.gsq.iart.viewmodel.MemberViewModel

class MemberTypeFragment: BaseFragment<MemberViewModel, FragmentMemberTypeBinding>() {

    private val args: MemberArgsType by args()

    override fun initView(savedInstanceState: Bundle?) {

    }

    companion object {

        const val TAG = "MemberTypeFragment"
        fun start(args: MemberArgsType): MemberTypeFragment {
            val fragment = MemberTypeFragment()
            fragment.arguments = args.asMavericksArgs()
            return fragment
        }
    }
}