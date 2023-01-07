package com.gsq.iart.app.weight.loadCallBack

import com.gsq.iart.R
import com.kingja.loadsir.callback.Callback


class EmptyWorksSearchCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_works_search_empty
    }

}