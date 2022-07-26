package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import androidx.core.os.bundleOf
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.databinding.FragmentSettingBinding
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import kotlinx.android.synthetic.main.fragment_setting.*

/**
 * 设置
 */
class SettingFragment: BaseFragment<BaseViewModel, FragmentSettingBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        title_layout.setBackListener {
            nav().navigateUp()
        }
        agreement_btn.setOnClickListener {
            nav().navigateAction(
                R.id.action_settingFragment_to_userAgreementFragment,
                bundleOf(UserAgreementFragment.INTENT_KEY_TYPE to UserAgreementFragment.INTENT_VALUE_USER_AGREEMENT)
            )
        }
        privacy_btn.setOnClickListener {
            nav().navigateAction(
                R.id.action_settingFragment_to_userAgreementFragment,
                bundleOf(UserAgreementFragment.INTENT_KEY_TYPE to UserAgreementFragment.INTENT_VALUE_SECRET_AGREEMENT)
            )
        }
    }

    override fun createObserver() {
        super.createObserver()
    }
}