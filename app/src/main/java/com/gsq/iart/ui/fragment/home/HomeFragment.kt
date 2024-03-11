package com.gsq.iart.ui.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ThreadUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.bindViewPager2
import com.gsq.iart.app.ext.init
import com.gsq.iart.app.ext.loadServiceInit
import com.gsq.iart.app.ext.showError
import com.gsq.iart.app.ext.showLoading
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.data.Constant
import com.gsq.iart.data.Constant.COMPLEX_TYPE_GROUP
import com.gsq.iart.data.bean.ArgsType
import com.gsq.iart.databinding.FragmentHomeBinding
import com.gsq.iart.viewmodel.HomeViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.visible
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_works_list.works_refresh_layout


/**
 * 首页
 */
class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    //fragment集合
    var fragments: ArrayList<Fragment> = arrayListOf()

    //标题集合
    var mDataList: ArrayList<String> = arrayListOf()

    //界面状态管理者
    private lateinit var loadsir: LoadService<Any>

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
            var bundle = Bundle()
            bundle.putInt(Constant.INTENT_DATA, 1)
            nav().navigateAction(R.id.action_mainFragment_to_searchFragment, bundle)
        }

        //状态页配置
        loadsir = loadServiceInit(home_view_pager) {
            //点击重试时触发的操作
            loadsir.showLoading()
            ThreadUtils.getMainHandler().postDelayed({
                lazyLoadData()
            },1000)
        }
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        //请求标题数据
        if(NetworkUtils.isConnected()){
            mViewModel.getClassifyList()
        }else{
            loadsir.showError(StringUtils.getString(R.string.http_error_data_retry))
        }
    }


    override fun createObserver() {
        super.createObserver()
        mViewModel.classifyList.observe(viewLifecycleOwner) {
            if (it != null) {
                loadsir.showSuccess()
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