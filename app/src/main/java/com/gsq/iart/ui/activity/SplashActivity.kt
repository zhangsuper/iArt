package com.gsq.iart.ui.activity

import android.content.Intent
import android.os.Bundle
import com.gsq.iart.app.base.BaseActivity
import com.gsq.iart.databinding.ActivitySplashBinding
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import kotlinx.android.synthetic.main.activity_splash.*

/**
 * 启动欢迎页
 */
class SplashActivity: BaseActivity<BaseViewModel, ActivitySplashBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        //防止出现按Home键回到桌面时，再次点击重新进入该界面bug
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT !== 0) {
            finish()
            return
        }

        splash_text.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            //带点渐变动画
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 500)

    }
}