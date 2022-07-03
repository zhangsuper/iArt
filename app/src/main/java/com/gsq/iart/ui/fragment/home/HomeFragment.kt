package com.gsq.iart.ui.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.bindViewPager2
import com.gsq.iart.app.ext.init
import com.gsq.iart.data.bean.HomeClassifyBean
import com.gsq.iart.databinding.FragmentHomeBinding
import com.gsq.iart.viewmodel.HomeViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment: BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    //fragment集合
    var fragments: ArrayList<Fragment> = arrayListOf()

    //标题集合
    var mDataList: ArrayList<String> = arrayListOf()

    override fun initView(savedInstanceState: Bundle?) {

        //初始化viewpager2
        home_view_pager.init(this, fragments)
        //初始化 magic_indicator
        home_magic_indicator.bindViewPager2(home_view_pager, mDataList)

        home_search_view.setOnClickListener {
            //跳转搜索节面
            nav().navigateAction(R.id.action_mainfragment_to_searchFragment)
        }

    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        //请求标题数据
//        mViewModel.getProjectTitleData()
    }


    override fun createObserver() {
        super.createObserver()
//        mViewModel.titleData.observe(viewLifecycleOwner, Observer { data ->
//
//        })

        var tablist = mutableListOf<HomeClassifyBean>()
        tablist.add(HomeClassifyBean(1,"国画"))
        tablist.add(HomeClassifyBean(2,"壁画"))
        tablist.add(HomeClassifyBean(3,"年画"))
        tablist.add(HomeClassifyBean(4,"刺绣"))
        tablist.add(HomeClassifyBean(5,"陶瓷"))
        tablist.add(HomeClassifyBean(6,"山水画"))
        tablist.add(HomeClassifyBean(7,"油画"))
        tablist.add(HomeClassifyBean(8,"素描"))
        fragments.clear()
        mDataList.clear()
        tablist.forEach { classify ->
            mDataList.add(classify.name)
            fragments.add(WorksListFragment.newInstance(classify))
        }

        home_magic_indicator.navigator.notifyDataSetChanged()
        home_view_pager.adapter?.notifyDataSetChanged()
        home_view_pager.offscreenPageLimit = fragments.size
    }
}