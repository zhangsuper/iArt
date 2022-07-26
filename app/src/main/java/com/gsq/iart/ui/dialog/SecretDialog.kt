package com.gsq.iart.ui.dialog

import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ScreenUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseDialog
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.databinding.DialogSecretBinding

/**
 * 隐私协议弹窗
 */
class SecretDialog: BaseDialog(R.layout.dialog_secret) {

    private val binding by viewBinding(DialogSecretBinding::bind)

    override fun changeDialogStyle() {
        isCancelable = false
        dialogWidth = (ScreenUtils.getScreenWidth()*0.9).toInt()
        dialogHeight = (ScreenUtils.getScreenHeight()*0.9).toInt()
        super.changeDialogStyle()
    }

    override fun initialize() {
        binding.agreeBtn.setOnClickListener {
            CacheUtil.setAgreePrivacyStatus(true)
            dismiss()
        }
        binding.disagreeBtn.setOnClickListener {
            CacheUtil.setAgreePrivacyStatus(false)
            AppUtils.exitApp()
        }
    }
}