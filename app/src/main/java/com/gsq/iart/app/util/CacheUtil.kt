package com.gsq.iart.app.util

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gsq.iart.data.bean.DictionaryWorksBean
import com.gsq.iart.data.bean.UserInfo
import com.gsq.iart.data.event.CompareEvent
import com.gsq.mvvm.ext.util.toJson
import com.tencent.mmkv.MMKV
import org.greenrobot.eventbus.EventBus

object CacheUtil {
    /**
     * 获取保存的账户信息
     */
    fun getUser(): UserInfo? {
        val kv = MMKV.mmkvWithID("app")
        val userStr = kv.decodeString("user")
        return if (TextUtils.isEmpty(userStr)) {
            null
        } else {
            Gson().fromJson(userStr, UserInfo::class.java)
        }
    }

    /**
     * 设置账户信息
     */
    fun setUser(userResponse: UserInfo?) {
        val kv = MMKV.mmkvWithID("app")
        if (userResponse == null) {
            kv.encode("user", "")
            setIsLogin(false)
        } else {
            kv.encode("user", Gson().toJson(userResponse))
            setIsLogin(true)
        }

    }

    /**
     * 是否已经登录
     */
    fun isLogin(): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.decodeBool("login", false)
    }

    /**
     * 设置是否已经登录
     */
    fun setIsLogin(isLogin: Boolean) {
        val kv = MMKV.mmkvWithID("app")
        kv.encode("login", isLogin)
    }

    /**
     * 是否是第一次登陆
     */
    fun isFirst(): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.decodeBool("first", true)
    }

    /**
     * 是否是第一次登陆
     */
    fun setFirst(first: Boolean): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.encode("first", first)
    }

    fun setWeChatLoginCode(code: String) {
        val kv = MMKV.mmkvWithID("app")
        kv.encode("wechat_code", code)
    }

    fun getWeChatLoginCode(): String? {
        val kv = MMKV.mmkvWithID("app")
        return kv.getString("wechat_code", "")
    }

    /**
     * 是否同意隐私政策
     */
    fun isAgreePrivacy(): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.decodeBool("agreePrivacy", false)
    }

    /**
     * 设置是否同意隐私政策
     */
    fun setAgreePrivacyStatus(isAgree: Boolean): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.encode("agreePrivacy", isAgree)
    }

    /**
     * 获取搜索历史缓存数据
     */
    fun getSearchHistoryData(): ArrayList<String> {
        val kv = MMKV.mmkvWithID("cache")
        val searchCacheStr = kv.decodeString("history")
        if (!TextUtils.isEmpty(searchCacheStr)) {
            return Gson().fromJson(searchCacheStr, object : TypeToken<ArrayList<String>>() {}.type)
        }
        return arrayListOf()
    }

    fun setSearchHistoryData(searchResponseStr: String) {
        val kv = MMKV.mmkvWithID("cache")
        kv.encode("history", searchResponseStr)
    }

    /**
     * 获取对比列表
     */
    fun getCompareList(): ArrayList<DictionaryWorksBean> {
        val kv = MMKV.mmkvWithID("app")
        val compareList = kv.decodeString("CompareList")
        if (!TextUtils.isEmpty(compareList)) {
            return Gson().fromJson(compareList, object : TypeToken<ArrayList<DictionaryWorksBean>>() {}.type)
        }
        return arrayListOf()
    }

    fun setCompareList(list: ArrayList<DictionaryWorksBean>){
        val kv = MMKV.mmkvWithID("app")
        kv.encode("CompareList", list.toJson())
        EventBus.getDefault().post(CompareEvent())
    }

    /**
     * 加入对比列表
     */
    fun addCompareList(item: DictionaryWorksBean){
        var compareList = getCompareList()
        compareList.add(0, item)
        val kv = MMKV.mmkvWithID("app")
        kv.encode("CompareList", compareList.toJson())
        EventBus.getDefault().post(CompareEvent())
    }

    fun removeCompare(item: DictionaryWorksBean){
        var compareList = getCompareList()
        var compareFilterList = compareList.filterNot { it.workId == item.workId }
        val kv = MMKV.mmkvWithID("app")
        kv.encode("CompareList", compareFilterList.toJson())
        EventBus.getDefault().post(CompareEvent())
    }
}