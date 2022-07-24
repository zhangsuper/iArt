package com.gsq.iart.app.util

import android.app.Activity
import androidx.fragment.app.Fragment
import com.gsq.iart.R
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar

// 设置沉浸式透明状态栏
object StatusBarUtil {

    fun init(
        activity: Activity,
        fitSystem: Boolean = true,
        isHideBar: Boolean = false,
        statusBarColor: Int = 0,
        isDarkFont: Boolean = true
    ) {
        val immersionBar = ImmersionBar.with(activity)

        immersionBar.fitsSystemWindows(fitSystem)

        if(isHideBar){
            immersionBar.hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
        }else{
            immersionBar.hideBar(BarHide.FLAG_SHOW_BAR)
        }

        if (statusBarColor != 0) {
            immersionBar.statusBarColor(statusBarColor)
        } else {
            immersionBar.transparentStatusBar()
        }

        immersionBar.statusBarDarkFont(isDarkFont)
            .navigationBarColor(R.color.white)
            .navigationBarDarkIcon(true)
            .init()
    }

    fun getStatusBarHeight(fragment: Fragment): Int {
        return ImmersionBar.getStatusBarHeight(fragment)
    }

}