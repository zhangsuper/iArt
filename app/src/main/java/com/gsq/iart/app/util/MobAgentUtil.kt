package com.gsq.iart.app.util

import com.blankj.utilcode.util.LogUtils
import com.gsq.iart.app.App
import com.umeng.analytics.MobclickAgent


object MobAgentUtil {

    fun onEvent(eventId: String, valueMap: MutableMap<String, Any?>? = null) {
        LogUtils.dTag("MobAgentUtil", "eventId: ${eventId},valueMap:${valueMap?.values}")
        if (valueMap == null) {
            var tempValue = mutableMapOf<String, Any?>()
            CacheUtil.getUser()?.userId?.let {
                tempValue["userid"] = CacheUtil.getUser()?.userId
                MobclickAgent.onEventObject(App.instance, eventId, tempValue)
            } ?: let {
                MobclickAgent.onEvent(App.instance, eventId)
            }
        } else {
            CacheUtil.getUser()?.userId?.let {
                valueMap["userid"] = CacheUtil.getUser()?.userId
            }
            MobclickAgent.onEventObject(App.instance, eventId, valueMap)
        }
    }
}