package com.gsq.iart.app

import androidx.multidex.MultiDex
import com.blankj.utilcode.util.LogUtils
import com.gsq.iart.app.weight.loadCallBack.EmptyCallback
import com.gsq.iart.app.weight.loadCallBack.ErrorCallback
import com.gsq.iart.app.weight.loadCallBack.LoadingCallback
import com.gsq.mvvm.base.BaseApp
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadSir
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mmkv.MMKV

class App : BaseApp() {

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this.filesDir.absolutePath + "/mmkv")
        instance = this
        MultiDex.install(this)
        //界面加载管理 初始化
        LoadSir.beginBuilder()
            .addCallback(LoadingCallback())//加载
            .addCallback(ErrorCallback())//错误
            .addCallback(EmptyCallback())//空
            .setDefaultCallback(SuccessCallback::class.java)//设置默认加载状态页
            .commit()
        initBugly()
        LogUtils.getConfig().globalTag = "my_tag"
    }

    private fun initBugly() {
        //初始化Bugly
//        if (!BuildConfig.DEBUG) {
        CrashReport.initCrashReport(applicationContext, "8bc600eefc", false)
//        }
    }
}