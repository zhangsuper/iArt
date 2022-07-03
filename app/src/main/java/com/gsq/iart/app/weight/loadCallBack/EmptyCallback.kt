package com.gsq.iart.app.weight.loadCallBack

import com.gsq.iart.R
import com.kingja.loadsir.callback.Callback


class EmptyCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_empty
    }

}