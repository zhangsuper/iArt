package com.gsq.iart.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.BuildConfig
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseActivity
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.app.util.WxLoginUtil
import com.gsq.iart.databinding.ActivityMainBinding
import com.gsq.iart.ui.dialog.SecretDialog
import com.gsq.iart.viewmodel.LoginViewModel
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.network.manager.NetState
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory


/**
 * 项目主页Activity
 */
class MainActivity : BaseActivity<BaseViewModel, ActivityMainBinding>() {

    var exitTime = 0L
    private lateinit var api: IWXAPI
    private var mLoginViewModel: LoginViewModel? = null

    @SuppressLint("ResourceAsColor")
    override fun initView(savedInstanceState: Bundle?) {
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
            SecretDialog().show(supportFragmentManager)
        }
        WxLoginUtil.initWx(this)
        if (CacheUtil.isLogin()) {
            mLoginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
            mLoginViewModel?.getUserInfo()
        }
    }

    override fun createObserver() {
        mLoginViewModel?.loginResultDataState?.observe(this) {
            if (it.isSuccess) {
                it.data?.let { userInfo ->
                    CacheUtil.setUser(userInfo)
                }
            }
        }
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
}