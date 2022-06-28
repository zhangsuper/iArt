package com.gsq.iart.ui.fragment

import android.os.Bundle
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.init
import com.gsq.iart.app.ext.initMain
import com.gsq.iart.app.ext.interceptLongClick
import com.gsq.iart.databinding.FragmentMainBinding
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment: BaseFragment<BaseViewModel, FragmentMainBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        //初始化viewpager2
        main_viewpager.initMain(this)
        //初始化 bottomBar
        mainBottom.init{
            when (it) {
                R.id.menu_home -> main_viewpager.setCurrentItem(0, false)
                R.id.menu_mine -> main_viewpager.setCurrentItem(1, false)
            }
        }
        mainBottom.interceptLongClick(R.id.menu_home,R.id.menu_mine)
    }

    override fun createObserver() {

    }
}