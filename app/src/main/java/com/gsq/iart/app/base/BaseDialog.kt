package com.gsq.iart.app.base

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.airbnb.mvrx.MavericksView
import com.blankj.utilcode.util.ScreenUtils
import com.gsq.iart.R

/**
 * 基类Dialog
 */
abstract class BaseDialog(@LayoutRes layoutId: Int) : DialogFragment(layoutId), MavericksView {

    protected var gravity = Gravity.CENTER
    protected var isShowAnim = true
    protected var dialogWidth =
        (ScreenUtils.getScreenWidth() * (if (ScreenUtils.isLandscape()) 0.4 else 0.75)).toInt()
    protected var dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(cancelable)
    }

    override fun getTheme(): Int {
        return R.style.BaseDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            dismiss()
        }
        changeDialogStyle()
        initialize()
    }

    /**
     * style中定义了一些基本样式
     * 代码层可以通过此方法继续丰富样式
     */
    open fun changeDialogStyle() {
        val window = dialog?.window
        val params = window?.attributes
        params?.gravity = gravity
        params?.width = dialogWidth
        params?.height = dialogHeight
        if (isShowAnim) {
            params?.windowAnimations = if (gravity == Gravity.BOTTOM) {
                R.style.BaseDialogAnimTranslate
            } else {
                R.style.BaseDialogAnimScale
            }
        }
        window?.attributes = params
        window?.decorView?.setPadding(0, 0, 0, 0)
    }

    abstract fun initialize()

    fun show(manager: FragmentManager) {
        show(manager, null)
    }


    override fun onDismiss(dialog: DialogInterface) {
        // ByLog.d("Step 3 Dialog onDismiss")
        dismissCallback?.invoke()
        dismissCallback = null
        super.onDismiss(dialog)
    }

    private var dismissCallback: (() -> Unit)? = null
    fun setOnDismissCallback(listener: (() -> Unit)?): BaseDialog {
        this.dismissCallback = listener
        return this
    }

    override fun invalidate() {
    }

}