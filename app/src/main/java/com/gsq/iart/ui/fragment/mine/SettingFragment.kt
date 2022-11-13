package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.databinding.FragmentSettingBinding
import com.gsq.iart.viewmodel.LoginViewModel
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.visible
import kotlinx.android.synthetic.main.fragment_setting.*

/**
 * 设置
 */
class SettingFragment : BaseFragment<BaseViewModel, FragmentSettingBinding>() {

    private var mLoginViewModel: LoginViewModel? = null

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
        personal_information_btn.setOnClickListener {
            nav().navigateAction(
                R.id.action_settingFragment_to_userAgreementFragment,
                bundleOf(UserAgreementFragment.INTENT_KEY_TYPE to UserAgreementFragment.INTENT_VALUE_PERSONAL_INFO)
            )
        }
        sdk_information_btn.setOnClickListener {
            nav().navigateAction(
                R.id.action_settingFragment_to_userAgreementFragment,
                bundleOf(UserAgreementFragment.INTENT_KEY_TYPE to UserAgreementFragment.INTENT_VALUE_SDK_INFO)
            )
        }
        login_out_btn.setOnClickListener {
            //退出登录
            mLoginViewModel?.logout()
        }
        cancellation_btn.setOnClickListener {
            //注销
            nav().navigateAction(
                R.id.action_settingFragment_to_writeOffFragment
            )
        }

        mLoginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        if (CacheUtil.isLogin()) {
            login_out_btn.visible()
            cancellation_btn.visible()
            personal_information_btn.setItemDividerVisible(true)
        } else {
            login_out_btn.gone()
            cancellation_btn.gone()
            personal_information_btn.setItemDividerVisible(false)
        }
    }

    override fun createObserver() {
        super.createObserver()
        mLoginViewModel?.logoutResultDataState?.observe(viewLifecycleOwner) {
            if (it.isSuccess) {
                ToastUtils.showLong("已退出登录")
                CacheUtil.setUser(null)
                nav().navigateUp()
            } else {
                ToastUtils.showLong(it.errorMsg)
            }
        }
        mLoginViewModel?.writeOffDataState?.observe(viewLifecycleOwner) {
            if (it.isSuccess) {
                ToastUtils.showLong("账号已注销")
                CacheUtil.setUser(null)
                nav().navigateUp()
            } else {
                ToastUtils.showLong(it.errorMsg)
            }
        }
    }
}