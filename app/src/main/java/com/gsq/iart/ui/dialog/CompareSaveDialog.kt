package com.gsq.iart.ui.dialog

import android.content.DialogInterface
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseDialog
import com.gsq.iart.databinding.DialogCompareSaveBinding
import com.gsq.mvvm.ext.view.onClick

class CompareSaveDialog : BaseDialog(R.layout.dialog_compare_save) {

    private val binding by viewBinding(DialogCompareSaveBinding::bind)

    private var mBackListener: ((name: String) -> Unit)? = null
    private var type: Int = 1//1:保存 2：修改
    private var content: String =""

    override fun changeDialogStyle() {
        isCancelable = true
        super.changeDialogStyle()
    }

    override fun initialize() {
        if (type == 2) {
            binding.tvTitle.text = "修改图单名称"
        } else {
            binding.tvTitle.text = "图单名称"
        }
        binding.cancelBtn.onClick {
            dismiss()
        }
        binding.sureBtn.onClick {
            if (binding.etName.text.isNullOrEmpty()) {
                ToastUtils.showShort("请输入图单名称！")
                return@onClick
            }
            mBackListener?.invoke(binding.etName.text.toString())
            dismiss()
        }
        binding.etName.setText(content)
        binding.etName.requestFocus()
        KeyboardUtils.showSoftInput()
    }

    fun setBackListener(listener: ((name: String) -> Unit)): CompareSaveDialog {
        mBackListener = listener
        return this
    }

    fun setDialogType(type: Int): CompareSaveDialog {
        this.type = type
        return this
    }

    fun setDialogContent(content: String?): CompareSaveDialog {
        content?.let {
            this.content = it
        }
        return this
    }

    override fun onDismiss(dialog: DialogInterface) {
        KeyboardUtils.hideSoftInput(binding.etName)
        super.onDismiss(dialog)
    }
}