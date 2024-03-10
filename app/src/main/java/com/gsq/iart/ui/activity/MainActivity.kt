package com.gsq.iart.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.gsq.iart.BuildConfig
import com.gsq.iart.R
import com.gsq.iart.app.App
import com.gsq.iart.app.base.BaseActivity
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.FileUtil
import com.gsq.iart.app.util.MobAgentUtil
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.app.util.WxLoginUtil
import com.gsq.iart.data.event.LoginEvent
import com.gsq.iart.databinding.ActivityMainBinding
import com.gsq.iart.ui.dialog.DialogUtils
import com.gsq.iart.ui.dialog.SecretDialog
import com.gsq.iart.viewmodel.AppViewModel
import com.gsq.iart.viewmodel.LoginViewModel
import com.gsq.mvvm.network.manager.NetState
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File


/**
 * 项目主页Activity
 */
class MainActivity : BaseActivity<AppViewModel, ActivityMainBinding>() {
    companion object {
        const val TAG = "MainActivity"
    }

    var exitTime = 0L
    private lateinit var api: IWXAPI
    private var mLoginViewModel: LoginViewModel? = null

    @SuppressLint("ResourceAsColor")
    override fun initView(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        StatusBarUtil.init(this)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val nav = Navigation.findNavController(this@MainActivity, R.id.main_fragment)
                if (nav.currentDestination != null && nav.currentDestination!!.id != R.id.mainFragment) {
                    //如果当前界面不是主页，那么直接调用返回即可
                    nav.navigateUp()
                } else {
                    //是主页
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        ToastUtils.showShort("再按一次退出程序")
                        exitTime = System.currentTimeMillis()
                    } else {
                        finish()
                    }
                }
            }
        })
        if (!CacheUtil.isAgreePrivacy()) {
            SecretDialog().setBackListener {
                if (CacheUtil.isAgreePrivacy()) {
                    initData()
                }
            }.show(supportFragmentManager)
        } else {
            initData()
        }
        if (CacheUtil.isLogin()) {
            mLoginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
            mLoginViewModel?.getUserInfo()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initData(){
        WxLoginUtil.initWx(this)
        ThreadUtils.getMainHandler().postDelayed({
            if (DialogUtils.normalDialog == null) {
                mViewModel.checkAppVersion(BuildConfig.VERSION_CODE.toString())
            } else {
                if (!DialogUtils.normalDialog.isShowing) {
                    mViewModel.checkAppVersion(BuildConfig.VERSION_CODE.toString())
                }
            }

        }, 1000)
        initBugly()
    }

    private fun initBugly() {
        //初始化Bugly
//        if (!BuildConfig.DEBUG) {
        CrashReport.initCrashReport(applicationContext, "8bc600eefc", false)
//        }
    }

    override fun createObserver() {
        mLoginViewModel?.loginResultDataState?.observe(this) {
            if (it.isSuccess) {
                it.data?.let { userInfo ->
                    CacheUtil.setUser(userInfo)
                }
            }
        }
        mViewModel.appVersionDataState.observe(this) {
            if (it.isSuccess) {
                it.data?.let { appVersion ->
                    if (appVersion.forceUpdate == 1) {
                        //强更
                        LogUtils.dTag(TAG, "appVersion.forceUpdate == 1")
                        DialogUtils.showNormalDoubleButtonDialog(
                            this,
                            "提示",
                            appVersion.desc,
                            "立即更新",
                            "",
                            false
                        ) { dialog, isLeft ->
                            if (!isLeft) {
                                //立即更新
                                if (NetworkUtils.isConnected()) {
                                    downloadApk(appVersion.url, appVersion.version)
                                    DialogUtils.showProgressDialog(this, "提示", "正在更新中...", false)
                                } else {
                                    ToastUtils.showShort(getString(R.string.http_error_net_disable))
                                }
                            }
                        }
                    } else {
                        //更新
                        LogUtils.dTag(TAG, "appVersion.forceUpdate != 1")
                        DialogUtils.showNormalDoubleButtonDialog(
                            this,
                            "提示",
                            appVersion.desc,
                            "立即更新",
                            "稍后更新",
                            true
                        ) { dialog, isLeft ->
                            if (!isLeft) {
                                //立即更新
                                if (NetworkUtils.isConnected()) {
                                    downloadApk(appVersion.url, appVersion.version)
                                    DialogUtils.showProgressDialog(this, "提示", "正在更新中...", false)
                                } else {
                                    ToastUtils.showShort(getString(R.string.http_error_net_disable))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun downloadApk(url: String, version: String) {
//        FileUtils.createOrExistsDir(Constant.DOWNLOAD_PARENT_PATH)
        val downloadId =
            PRDownloader.download(
                url,
                FileUtil.getPrivateSavePath("download"),
                "art_${version}.apk"
            )
                .build()
                .setOnStartOrResumeListener {
//                progress_bar.visible()
                }
                .setOnPauseListener { }
                .setOnCancelListener {
//                progress_bar.gone()
                }
                .setOnProgressListener {
                    var progress = (it.currentBytes * 100 / it.totalBytes).toInt()
                    LogUtils.dTag(TAG, "downloadApk progress:${progress}")
                    runOnUiThread {
                        DialogUtils.updateProgress(progress)
                    }
                }
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {
                        //安装
                        installApk("${FileUtil.getPrivateSavePath("download")}art_${version}.apk")
                        DialogUtils.dismissProgressDialog()
                    }

                    override fun onError(error: com.downloader.Error?) {
                        ToastUtils.showLong("下载失败！")
                        DialogUtils.dismissProgressDialog()
                    }
                })
    }

    /**
     * 安装apk
     */
    private fun installApk(path: String) {
        val apkfile = File(path)
        if (!apkfile.exists()) {
            return
        }
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        //兼容android7.0以上版本
        var uri: Uri? = Uri.fromFile(apkfile)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //通过FileProvider创建一个content类型的Uri
            uri = FileProvider.getUriForFile(App.instance, "$packageName.provider", apkfile)
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        App.instance.startActivity(intent)
    }

    /**
     * 示例，在Activity/Fragment中如果想监听网络变化，可重写onNetworkStateChanged该方法
     */
    override fun onNetworkStateChanged(netState: NetState) {
        super.onNetworkStateChanged(netState)
        if (netState.isSuccess) {

        } else {

        }
    }

    private fun regToWeChat() {
        // 通过 WXAPIFactory 工厂，获取 IWXAPI 的实例
        api = WXAPIFactory.createWXAPI(this, BuildConfig.WEIXIN_KEY, true)
        // 将应用的 appId 注册到微信
        api.registerApp(BuildConfig.WEIXIN_KEY)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LoginEvent?) {
        event?.code?.let {
            if (it == "A0401") {
                //token过期
                var eventMap = mutableMapOf<String, Any?>()
                eventMap["user_id"] = CacheUtil.getUser()?.userId
                MobAgentUtil.onEvent("token_expired", eventMap)
                CacheUtil.setUser(null)
            }
        }
    }
}