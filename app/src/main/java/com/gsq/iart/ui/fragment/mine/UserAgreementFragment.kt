package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.network.ApiService.Companion.agreement_url
import com.gsq.iart.app.network.ApiService.Companion.personal_info_url
import com.gsq.iart.app.network.ApiService.Companion.privacy_url
import com.gsq.iart.app.network.ApiService.Companion.sdk_info_url
import com.gsq.iart.app.network.ApiService.Companion.vip_agreement_url
import com.gsq.iart.databinding.FragmentUserAgreementBinding
import com.gsq.iart.viewmodel.WebViewModel
import com.gsq.mvvm.ext.nav
import com.just.agentweb.AgentWeb
import kotlinx.android.synthetic.main.fragment_user_agreement.*

/**
 * 用户协议，隐私政策
 */
class UserAgreementFragment : BaseFragment<WebViewModel, FragmentUserAgreementBinding>() {


    companion object {
        const val INTENT_KEY_TYPE = "data"
        const val INTENT_VALUE_USER_AGREEMENT = "user_agreement"//用户协议
        const val INTENT_VALUE_SECRET_AGREEMENT = "secret_agreement"//隐私协议
        const val INTENT_VALUE_VIP_AGREEMENT = "vip_agreement"//会员服务协议
        const val INTENT_VALUE_PERSONAL_INFO = "personal_info"//个人信息协议
        const val INTENT_VALUE_SDK_INFO = "sdk_info"//第三方服务列表
    }

    private var agreementType: String? = null

    private var mAgentWeb: AgentWeb? = null

    private var preWeb: AgentWeb.PreAgentWeb? = null

    override fun initView(savedInstanceState: Bundle?) {
        agreementType = arguments?.getString(INTENT_KEY_TYPE)
        if (agreementType == INTENT_VALUE_USER_AGREEMENT) {
            mViewModel.showTitle = getString(R.string.app_agreement)
            mViewModel.url = agreement_url
        } else if (agreementType == INTENT_VALUE_SECRET_AGREEMENT) {
            mViewModel.showTitle = getString(R.string.app_privacy)
            mViewModel.url = privacy_url
        } else if (agreementType == INTENT_VALUE_VIP_AGREEMENT) {
            mViewModel.showTitle = getString(R.string.vip_agreement)
            mViewModel.url = vip_agreement_url
        } else if (agreementType == INTENT_VALUE_PERSONAL_INFO) {
            mViewModel.showTitle = getString(R.string.collect_personal_information)
            mViewModel.url = personal_info_url
        } else if (agreementType == INTENT_VALUE_SDK_INFO) {
            mViewModel.showTitle = getString(R.string.sdk_information)
            mViewModel.url = sdk_info_url
        }
        title_layout.setBackListener {
            nav().navigateUp()
        }

        preWeb = AgentWeb.with(this)
            .setAgentWebParent(web_content, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .createAgentWeb()
            .ready()
    }

    override fun lazyLoadData() {
        //加载网页
        title_layout.setTitle(mViewModel.showTitle)
        mAgentWeb = preWeb?.go(mViewModel.url)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    mAgentWeb?.let { web ->
                        if (web.webCreator.webView.canGoBack()) {
                            web.webCreator.webView.goBack()
                        } else {
                            nav().navigateUp()
                        }
                    }
                }
            })
    }

    override fun createObserver() {
        super.createObserver()
    }

    override fun onPause() {
        mAgentWeb?.webLifeCycle?.onPause()
        super.onPause()
    }

    override fun onResume() {
        mAgentWeb?.webLifeCycle?.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        mAgentWeb?.webLifeCycle?.onDestroy()
        mActivity.setSupportActionBar(null)
        super.onDestroy()
    }
}