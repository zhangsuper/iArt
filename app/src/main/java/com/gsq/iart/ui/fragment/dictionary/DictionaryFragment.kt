package com.gsq.iart.ui.fragment.dictionary

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ThreadUtils
import com.gsq.iart.BuildConfig
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.bindViewPager2
import com.gsq.iart.app.ext.init
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.data.Constant
import com.gsq.iart.data.bean.WorksBean
import com.gsq.iart.databinding.FragmentDictionaryBinding
import com.gsq.iart.ui.adapter.AllConditionAdapter
import com.gsq.iart.ui.adapter.DictionaryMenuAdapter
import com.gsq.iart.ui.fragment.mine.MemberFragment
import com.gsq.iart.viewmodel.DictionaryViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.onClick
import com.gsq.mvvm.ext.view.visible
import kotlinx.android.synthetic.main.fragment_dictionary.open_vip_btn
import kotlinx.android.synthetic.main.fragment_dictionary.recycler_view
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * 图典
 */
class DictionaryFragment : BaseFragment<DictionaryViewModel, FragmentDictionaryBinding>() {

    private var mAdapter: DictionaryMenuAdapter? = null
    private var isClickVipBtn = false

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
        if(isClickVipBtn){
            isClickVipBtn = false
            mViewModel.getDictionaryClassifyList()//请求图典菜单列表
        }

    }

    override fun initView(savedInstanceState: Bundle?) {
        home_search_view.setOnClickListener {
            //跳转搜索节面
            nav().navigateAction(R.id.action_mainFragment_to_searchFragment)
        }
        mAdapter = DictionaryMenuAdapter()
        mAdapter!!.setClickBackListener { bean, position ->
            var bundle = Bundle()
            bundle.putSerializable(Constant.INTENT_DATA, bean)
            bundle.putInt(Constant.INTENT_POSITION, position)
            nav().navigateAction(R.id.action_mainFragment_to_dictionaryListFragment, bundle)
        }
        mAdapter!!.setExtendClickListener {
            if(CacheUtil.getUser()?.memberType != 1 && !BuildConfig.DEBUG){
                nav().navigateAction(
                    R.id.action_mainFragment_to_memberFragment,
                    bundleOf(MemberFragment.INTENT_KEY_TYPE to MemberFragment.INTENT_VALUE_WORKS)
                )
            }else{
                recycler_view.scrollToPosition(mAdapter!!.data.size - 1)
            }
        }
        recycler_view.adapter = mAdapter
        open_vip_btn.onClick {
            isClickVipBtn = true
            nav().navigateAction(
                R.id.action_mainFragment_to_memberFragment,
                bundleOf(MemberFragment.INTENT_KEY_TYPE to MemberFragment.INTENT_VALUE_WORKS)
            )
        }
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        mViewModel.getDictionaryClassifyList()//请求图典菜单列表
    }


    override fun createObserver() {
        super.createObserver()
        mViewModel.classifyList.observe(viewLifecycleOwner){
            if(it!=null){
                if (it.size>3 && CacheUtil.getUser()?.memberType != 1 && !BuildConfig.DEBUG) {
                    mAdapter?.data = it.subList(0,3)
                    mAdapter?.notifyDataSetChanged()
                    //需要付费且没有开通了会员
                    open_vip_btn.visible()
                } else {
                    mAdapter?.data = it
                    mAdapter?.notifyDataSetChanged()
                    open_vip_btn.gone()
                }
            }
        }
    }

}