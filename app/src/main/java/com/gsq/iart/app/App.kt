package com.gsq.iart.app

import androidx.multidex.MultiDex
import com.blankj.utilcode.util.LogUtils
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.gsq.iart.BuildConfig
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.weight.loadCallBack.*
import com.gsq.mvvm.base.BaseApp
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadSir
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mmkv.MMKV
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure


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
            .addCallback(EmptyWorksSearchCallback())//作品搜索空页面
            .addCallback(EmptyWorksCollectCallback())//作品收藏空页面
            .setDefaultCallback(SuccessCallback::class.java)//设置默认加载状态页
            .commit()
        initBugly()
        LogUtils.getConfig().globalTag = "my_tag"

        val config = PRDownloaderConfig.newBuilder()
            .setDatabaseEnabled(true)
            .setReadTimeout(30000)
            .setConnectTimeout(30000)
            .build()
        PRDownloader.initialize(applicationContext, config)

        initUmeng()
    }

    private fun initBugly() {
        //初始化Bugly
//        if (!BuildConfig.DEBUG) {
        CrashReport.initCrashReport(applicationContext, "8bc600eefc", false)
//        }
    }

    private fun initUmeng() {
        UMConfigure.preInit(this, "63833c5488ccdf4b7e716bb1", BuildConfig.CHANNEL)
        if (CacheUtil.isAgreePrivacy()) {
            UMConfigure.init(
                this,
                "63833c5488ccdf4b7e716bb1",
                BuildConfig.CHANNEL,
                UMConfigure.DEVICE_TYPE_PHONE,
                ""
            )
            // 选用手动页面采集模式
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL)
//            UMConfigure.setLogEnabled(true)
        }
        //友盟正式初始化
//        var umInitConfig = UmInitConfig()
//        umInitConfig.UMinit(getApplicationContext());
    }
}