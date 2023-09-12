package com.gsq.iart.ui.fragment.dictionary

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.bindViewPager2
import com.gsq.iart.app.ext.init
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.databinding.FragmentDictionaryBinding
import com.gsq.iart.viewmodel.DictionaryViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * 图典
 */
class DictionaryFragment : BaseFragment<DictionaryViewModel, FragmentDictionaryBinding>() {

    //fragment集合
    var fragments: ArrayList<Fragment> = arrayListOf()

    //标题集合
    var mDataList: ArrayList<String> = arrayListOf()

    override fun onResume() {
        super.onResume()
        StatusBarUtil.init(
            requireActivity(),
            fitSystem = true,
            statusBarColor = R.color.white,
            isDarkFont = true
        )
    }

    override fun initView(savedInstanceState: Bundle?) {
        //初始化viewpager2
        home_view_pager.init(this, fragments)
        //初始化 magic_indicator
        home_magic_indicator.bindViewPager2(home_view_pager, mDataList)

        home_search_view.setOnClickListener {
            //跳转搜索节面
            nav().navigateAction(R.id.action_mainFragment_to_searchFragment)
        }
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
    }


    override fun createObserver() {
        super.createObserver()

    }
}