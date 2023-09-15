package com.gsq.iart.ui.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ThreadUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.bindViewPager2
import com.gsq.iart.app.ext.init
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.data.Constant.COMPLEX_TYPE_GROUP
import com.gsq.iart.data.bean.ArgsType
import com.gsq.iart.databinding.FragmentHomeBinding
import com.gsq.iart.viewmodel.HomeViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.visible
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * 首页
 */
class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    //fragment集合
    var fragments: ArrayList<Fragment> = arrayListOf()

    //标题集合
    var mDataList: ArrayList<String> = arrayListOf()

    override fun onResume() {
        super.onResume()
        ThreadUtils.getMainHandler().postDelayed({
            StatusBarUtil.init(
                requireActivity(),
                fitSystem = true,
                statusBarColor = R.color.white,
                isDarkFont = true
            )
        },100)
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
        //请求标题数据
        mViewModel.getClassifyList()
    }


    override fun createObserver() {
        super.createObserver()
        mViewModel.classifyList.observe(viewLifecycleOwner) {
            if (it != null) {
                fragments.clear()
                mDataList.clear()
                it.forEach { classify ->
                    mDataList.add(classify.name)
                    fragments.add(
                        WorksListFragment.start(
                            ArgsType(
                                COMPLEX_TYPE_GROUP,
                                classify.id
                            )
                        )
                    )
                }

                home_magic_indicator.navigator.notifyDataSetChanged()
                home_view_pager.adapter?.notifyDataSetChanged()
                home_view_pager.offscreenPageLimit = fragments.size
                if (fragments.size > 1) {
                    home_magic_indicator.visible()
                } else {
                    home_magic_indicator.gone()
                }
            }
        }
//        var tablist = mutableListOf<HomeClassifyBean>()
//        tablist.add(HomeClassifyBean(0,"国画"))

    }
}