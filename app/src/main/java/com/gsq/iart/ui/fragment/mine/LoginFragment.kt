package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.databinding.FragmentLoginBinding
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.nav
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
        SpanUtils.with(agreement_privacy)
            .append("我已阅读并同意")
            .append("《用户协议》").setForegroundColor(resources.getColor(R.color.color_141414))
            .append("和")
            .append("《隐私政策》").setQuoteColor(resources.getColor(R.color.color_141414))
    }

    override fun createObserver() {
        super.createObserver()
    }
}