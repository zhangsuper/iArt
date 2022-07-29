package com.gsq.iart.ui.fragment.mine

import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.os.Bundle
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.databinding.FragmentLoginBinding
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * 登录
 */
class LoginFragment: BaseFragment<BaseViewModel, FragmentLoginBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        iv_close.setOnClickListener {
            nav().navigateUp()
        }

        wechat_btn.setOnClickListener {
            if(!checkbox.isChecked){
                ToastUtils.showShort("请勾选同意《用户协议》和《隐私政策》")
                return@setOnClickListener
            }
            //微信登录
            ToastUtils.showShort("微信登录")
        }
        qq_btn.setOnClickListener {
            if(!checkbox.isChecked){
                ToastUtils.showShort("请勾选同意《用户协议》和《隐私政策》")
                return@setOnClickListener
            }
            //QQ登录
            ToastUtils.showShort("QQ登录")
        }
        val clickableSpan1 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                //用户协议
                (widget as TextView).highlightColor = resources.getColor(android.R.color.transparent)
                nav().navigateAction(R.id.action_loginFragment_to_userAgreementFragment,
                    bundleOf(UserAgreementFragment.INTENT_KEY_TYPE to UserAgreementFragment.INTENT_VALUE_USER_AGREEMENT)
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        val clickableSpan2 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                //隐私政策
                (widget as TextView).highlightColor = resources.getColor(android.R.color.transparent)
                nav().navigateAction(R.id.action_loginFragment_to_userAgreementFragment,
                    bundleOf(UserAgreementFragment.INTENT_KEY_TYPE to UserAgreementFragment.INTENT_VALUE_SECRET_AGREEMENT)
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        SpanUtils.with(agreement_privacy)
            .append("我已阅读并同意")
            .append("《用户协议》").setForegroundColor(resources.getColor(R.color.color_141414)).setClickSpan(clickableSpan1)
            .append("和")
            .append("《隐私政策》").setForegroundColor(resources.getColor(R.color.color_141414)).setClickSpan(clickableSpan2)
            .create()
    }

    override fun createObserver() {
        super.createObserver()
    }
}