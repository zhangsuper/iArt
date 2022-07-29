package com.gsq.iart.app.weight.loadCallBack

import android.content.Context
import android.view.View
import com.gsq.iart.R
import com.kingja.loadsir.callback.Callback


class LoadingCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_loading
    }

    override fun onReloadEvent(context: Context?, view: View?): Boolean {
        return true
    }
}