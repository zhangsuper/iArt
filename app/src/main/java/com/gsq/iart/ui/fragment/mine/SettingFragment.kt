package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.databinding.FragmentSettingBinding
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.nav
import kotlinx.android.synthetic.main.fragment_setting.*

/**
 * 设置
 */
class SettingFragment: BaseFragment<BaseViewModel, FragmentSettingBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        title_layout.setBackListener {
            nav().navigateUp()
        }
    }

    override fun createObserver() {
        super.createObserver()
    }
}