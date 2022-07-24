package com.gsq.iart.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseActivity
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.databinding.ActivityMainBinding
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.network.manager.NetState

/**
 * 项目主页Activity
 */
class MainActivity: BaseActivity<BaseViewModel, ActivityMainBinding>() {

    var exitTime = 0L
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
    }

    override fun createObserver() {
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
}