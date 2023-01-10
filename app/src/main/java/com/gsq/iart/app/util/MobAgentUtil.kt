package com.gsq.iart.app.util

import com.blankj.utilcode.util.LogUtils
import com.gsq.iart.app.App
import com.umeng.analytics.MobclickAgent


object MobAgentUtil {

    fun onEvent(eventId: String, valueMap: MutableMap<String, Any?>? = null) {
        MobclickAgent.onEventObject(App.instance, eventId, valueMap)
        LogUtils.dTag("MobAgentUtil", "eventId: ${eventId},valueMap:${valueMap?.values}")
    }
}