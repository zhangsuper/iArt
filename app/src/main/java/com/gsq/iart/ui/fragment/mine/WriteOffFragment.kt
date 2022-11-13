package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import android.widget.LinearLayout
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.network.ApiService.Companion.write_off_remind_url
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.databinding.FragmentWriteOffBinding
import com.gsq.iart.viewmodel.LoginViewModel
import com.gsq.mvvm.ext.nav
import com.just.agentweb.AgentWeb
import kotlinx.android.synthetic.main.fragment_setting.title_layout
import kotlinx.android.synthetic.main.fragment_write_off.*

/**
 * 账号注销
 */
class WriteOffFragment : BaseFragment<LoginViewModel, FragmentWriteOffBinding>() {

    private var mAgentWeb: AgentWeb? = null
    private var preWeb: AgentWeb.PreAgentWeb? = null

    override fun initView(savedInstanceState: Bundle?) {
        title_layout.setBackListener {
            nav().navigateUp()
        }
        write_off_btn.setOnClickListener {
            //注销
            mViewModel?.writeOff()
        }

        preWeb = AgentWeb.with(this)
            .setAgentWebParent(web_content, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .createAgentWeb()
            .ready()

        mAgentWeb = preWeb?.go(write_off_remind_url)
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel?.writeOffDataState?.observe(viewLifecycleOwner) {
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