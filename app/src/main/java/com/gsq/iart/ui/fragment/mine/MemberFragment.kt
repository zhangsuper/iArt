package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.databinding.FragmentMemberBinding
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.nav
import kotlinx.android.synthetic.main.fragment_member.*

/**
 * 我的会员
 */
class MemberFragment: BaseFragment<BaseViewModel,FragmentMemberBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        title_layout.setBackListener {
            nav().navigateUp()

        }
    }
}