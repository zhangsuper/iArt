package com.gsq.iart.app.util

import com.gsq.iart.app.App
import com.umeng.analytics.MobclickAgent


object MobAgentUtil {

    fun onEvent(eventId: String, valueMap: MutableMap<String, Any?>? = null) {
        MobclickAgent.onEventObject(App.instance, eventId, valueMap)
    }
}