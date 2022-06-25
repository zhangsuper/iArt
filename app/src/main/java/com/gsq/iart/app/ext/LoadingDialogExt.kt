package com.gsq.iart.app.ext

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog

//loading框
private var loadingDialog: MaterialDialog? = null

/**
 * 打开等待框
 */
fun AppCompatActivity.showLoadingExt(message: String = "请求网络中") {
    if (!this.isFinishing) {
//        if (loadingDialog == null) {
//            loadingDialog = MaterialDialog(this)
//                    .cancelable(true)
//                    .cancelOnTouchOutside(false)
//                    .cornerRadius(12f)
//                    .customView(R.layout.layout_custom_progress_dialog_view)
//                    .lifecycleOwner(this)
//            loadingDialog?.getCustomView()?.run {
//                this.findViewById<TextView>(R.id.loading_tips).text = message
//                this.findViewById<ProgressBar>(R.id.progressBar).indeterminateTintList = SettingUtil.getOneColorStateList(this@showLoadingExt)
//            }
//        }
//        loadingDialog?.show()
    }
}

/**
 * 打开等待框
 */
fun Fragment.showLoadingExt(message: String = "请求网络中") {
    activity?.let {
//        if (!it.isFinishing) {
//            if (loadingDialog == null) {
//                loadingDialog = MaterialDialog(it)
//                    .cancelable(true)
//                    .cancelOnTouchOutside(false)
//                    .cornerRadius(12f)
//                    .customView(R.layout.layout_custom_progress_dialog_view)
//                    .lifecycleOwner(this)
//                loadingDialog?.getCustomView()?.run {
//                    this.findViewById<TextView>(R.id.loading_tips).text = message
//                    this.findViewById<ProgressBar>(R.id.progressBar).indeterminateTintList = SettingUtil.getOneColorStateList(it)
//                }
//            }
//            loadingDialog?.show()
//        }
    }
}

/**
 * 关闭等待框
 */
fun Activity.dismissLoadingExt() {
    loadingDialog?.dismiss()
    loadingDialog = null
}

/**
 * 关闭等待框
 */
fun Fragment.dismissLoadingExt() {
    loadingDialog?.dismiss()
    loadingDialog = null
}
