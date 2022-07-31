package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.databinding.FragmentUserAgreementBinding
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.nav
import kotlinx.android.synthetic.main.fragment_user_agreement.*

/**
 * 用户协议，隐私政策
 */
class UserAgreementFragment: BaseFragment<BaseViewModel, FragmentUserAgreementBinding>() {


    companion object {
        const val INTENT_KEY_TYPE = "data"
        const val INTENT_VALUE_USER_AGREEMENT = "user_agreement"//用户协议
        const val INTENT_VALUE_SECRET_AGREEMENT = "secret_agreement"//隐私协议
        const val INTENT_VALUE_VIP_AGREEMENT = "vip_agreement"//会员服务协议
    }
    private var agreementType: String? = null

    override fun initView(savedInstanceState: Bundle?) {
        agreementType = arguments?.getString(INTENT_KEY_TYPE)
        if(agreementType == INTENT_VALUE_USER_AGREEMENT){
            title_layout.setTitle(getString(R.string.app_agreement))
        }else if(agreementType == INTENT_VALUE_SECRET_AGREEMENT){
            title_layout.setTitle(getString(R.string.app_privacy))
        }else if(agreementType == INTENT_VALUE_VIP_AGREEMENT){
            title_layout.setTitle(getString(R.string.vip_agreement))
        }
        title_layout.setBackListener {
            nav().navigateUp()
        }
    }

    override fun createObserver() {
        super.createObserver()
    }
}