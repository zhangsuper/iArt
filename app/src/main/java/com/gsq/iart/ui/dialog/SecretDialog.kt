package com.gsq.iart.ui.dialog

import android.widget.LinearLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ScreenUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseDialog
import com.gsq.iart.app.network.ApiService.Companion.personal_url
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.MobAgentUtil
import com.gsq.iart.databinding.DialogSecretBinding
import com.just.agentweb.AgentWeb
import com.umeng.commonsdk.UMConfigure
import kotlinx.android.synthetic.main.fragment_user_agreement.*

/**
 * 隐私协议弹窗
 */
class SecretDialog : BaseDialog(R.layout.dialog_secret) {

    private val binding by viewBinding(DialogSecretBinding::bind)
    private var mAgentWeb: AgentWeb? = null
    private var preWeb: AgentWeb.PreAgentWeb? = null

    override fun changeDialogStyle() {
        isCancelable = false
        dialogWidth = (ScreenUtils.getScreenWidth() * 0.9).toInt()
        dialogHeight = (ScreenUtils.getScreenHeight() * 0.9).toInt()
        super.changeDialogStyle()
    }

    override fun initialize() {
        binding.agreeBtn.setOnClickListener {
            CacheUtil.setAgreePrivacyStatus(true)
            dismiss()
            UMConfigure.init(
                binding.agreeBtn.context, "63833c5488ccdf4b7e716bb1", "yishuguan",
                UMConfigure.DEVICE_TYPE_PHONE, ""
            )
            MobAgentUtil.onEvent("policy")
        }
        binding.disagreeBtn.setOnClickListener {
            CacheUtil.setAgreePrivacyStatus(false)
            AppUtils.exitApp()
        }
        binding.ivBack.setOnClickListener {
            mAgentWeb?.back()
        }
        preWeb = AgentWeb.with(this)
            .setAgentWebParent(web_content, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .createAgentWeb()
            .ready()
        mAgentWeb = preWeb?.go(personal_url)

    }
}