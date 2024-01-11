package com.gsq.iart.ui.fragment.dictionary

import android.os.Bundle
import androidx.core.os.bundleOf
import com.blankj.utilcode.util.ThreadUtils
import com.gsq.iart.BuildConfig
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.data.Constant
import com.gsq.iart.data.bean.DictionarySetsBean
import com.gsq.iart.data.event.LoginSuccessEvent
import com.gsq.iart.data.event.LogoutEvent
import com.gsq.iart.databinding.FragmentDictionaryBinding
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
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * 图典
 */
class DictionaryFragment : BaseFragment<DictionaryViewModel, FragmentDictionaryBinding>() {

    private var mAdapter: DictionaryMenuAdapter? = null
    private var isClickVipBtn = false
    private var intent_data:DictionarySetsBean? = null

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
        EventBus.getDefault().register(this)
        intent_data = arguments?.getSerializable(Constant.INTENT_DATA) as? DictionarySetsBean
        home_search_view.setOnClickListener {
            //跳转搜索节面
            var bundle = Bundle()
            bundle.putInt(Constant.INTENT_DATA, 2)
            nav().navigateAction(R.id.action_mainFragment_to_searchFragment, bundle)
        }
        mAdapter = DictionaryMenuAdapter()
        mAdapter!!.setClickBackListener { bean, position ->
            var bundle = Bundle()
            bundle.putSerializable(Constant.INTENT_DATA, bean)
            bundle.putInt(Constant.INTENT_POSITION, position)
            intent_data?.let {
                bundle.putSerializable(Constant.INTENT_DATA_SUB, it)
            }
            nav().navigateAction(R.id.action_mainFragment_to_dictionaryListFragment, bundle)
        }
        mAdapter!!.setExtendClickListener {
            if(CacheUtil.getUserVipStatus() != 99 && !BuildConfig.DEBUG){
                nav().navigateAction(
                    R.id.action_mainFragment_to_memberFragment,
                    bundleOf(MemberFragment.INTENT_KEY_TYPE to MemberFragment.INTENT_VALUE_DICTIONARY)
                )
            }else{
                recycler_view.scrollToPosition(mAdapter!!.data.size - 1)
            }
        }
        recycler_view.adapter = mAdapter
        intent_data?.let {
            mViewBind.ivClose.visible()
        }?: let {
            mViewBind.ivClose.gone()
        }
        open_vip_btn.onClick {
            isClickVipBtn = true
            nav().navigateAction(
                R.id.action_mainFragment_to_memberFragment,
                bundleOf(MemberFragment.INTENT_KEY_TYPE to MemberFragment.INTENT_VALUE_DICTIONARY)
            )
        }
        mViewBind.ivClose.onClick {
            nav().popBackStack()
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
                if (it.size>6 && CacheUtil.getUserVipStatus() != 99 && !BuildConfig.DEBUG) {
                    mAdapter?.data = it.subList(0,6)
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

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LogoutEvent?) {
        event?.let {
            mViewModel.getDictionaryClassifyList()//请求图典菜单列表
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LoginSuccessEvent?) {
        event?.let {
            mViewModel.getDictionaryClassifyList()//请求图典菜单列表
        }
    }

}