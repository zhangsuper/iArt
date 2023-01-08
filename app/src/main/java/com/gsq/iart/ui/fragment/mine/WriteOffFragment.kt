package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import androidx.core.os.bundleOf
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.databinding.FragmentWriteOffBinding
import com.gsq.iart.viewmodel.LoginViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.gone
import kotlinx.android.synthetic.main.fragment_setting.title_layout
import kotlinx.android.synthetic.main.fragment_write_off.*

/**
 * 账号注销
 */
class WriteOffFragment : BaseFragment<LoginViewModel, FragmentWriteOffBinding>() {

//    private var mAgentWeb: AgentWeb? = null
//    private var preWeb: AgentWeb.PreAgentWeb? = null

    override fun initView(savedInstanceState: Bundle?) {
        title_layout.setBackListener {
            nav().navigateUp()
        }
        write_off_btn.setOnClickListener {
            //注销
            mViewModel?.writeOff()
        }

//        preWeb = AgentWeb.with(this)
//            .setAgentWebParent(web_content, LinearLayout.LayoutParams(-1, -1))
//            .useDefaultIndicator()
//            .createAgentWeb()
//            .ready()
//
//        mAgentWeb = preWeb?.go(write_off_remind_url)
        SpanUtils.with(write_text)
            .append("轻触下方的\"申请注销\"按钮，即表示您已阅读并同意")
            .setQuoteColor(ColorUtils.getColor(R.color.color_EFF1F5))
            .append("《重要提醒》")
            .setClickSpan(ColorUtils.getColor(R.color.color_141414), false) {
                nav().navigateAction(
                    R.id.action_writeOffFragment_to_userAgreementFragment,
                    bundleOf(UserAgreementFragment.INTENT_KEY_TYPE to UserAgreementFragment.INTENT_VALUE_WRITE_OFF)
                )
            }
            .create()
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel?.writeOffDataState?.observe(viewLifecycleOwner) {
            if (it.isSuccess) {
                write_off_tips.gone()
                write_text.gone()
                write_off_btn.gone()
                ToastUtils.showLong("账号已注销")
                CacheUtil.setUser(null)
                CacheUtil.setIsLogin(false)
//                nav().navigateUp()
            } else {
                ToastUtils.showLong(it.errorMsg)
            }
        }
    }
}