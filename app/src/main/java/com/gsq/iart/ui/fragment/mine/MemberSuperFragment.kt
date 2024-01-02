package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.MobAgentUtil
import com.gsq.iart.data.bean.PayConfigBean
import com.gsq.iart.databinding.FragmentMemberSuperBinding
import com.gsq.iart.ui.adapter.VipPriceAdapter
import com.gsq.iart.viewmodel.MemberViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.onClick

class MemberSuperFragment: BaseFragment<MemberViewModel, FragmentMemberSuperBinding>() {

    private val vipPriceAdapter: VipPriceAdapter by lazy { VipPriceAdapter() }


    override fun initView(savedInstanceState: Bundle?) {
        mViewBind.priceRecyclerView.adapter = vipPriceAdapter
        vipPriceAdapter.setOnItemClickListener { adapter, view, position ->
            vipPriceAdapter.selectItem(adapter.getItem(position) as PayConfigBean)
        }

        mViewBind.payButton.onClick {
            vipPriceAdapter.selectBean?.let {
                if (!mViewBind.checkbox.isChecked) {
                    ToastUtils.showShort("请勾选同意《会员服务协议》")
                    return@let
                }
                if (!CacheUtil.isLogin()) {
                    nav().navigateAction(R.id.action_memberFragment_to_loginFragment)
                    var eventMap = mutableMapOf<String, Any?>()
                    eventMap["type"] = "vip"
                    MobAgentUtil.onEvent("signin", eventMap)
                    return@let
                }
                mViewModel.createPreparePay(it.id)
                var eventMap = mutableMapOf<String, Any?>()
                eventMap["payType"] = "wechat"
                MobAgentUtil.onEvent("vip_buy", eventMap)
            } ?: let {
                ToastUtils.showLong("请选择开通套餐")
            }
        }
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        mViewModel.getPayConfig(99)
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.payConfigDataState.observe(viewLifecycleOwner) {
            if (!it.isSuccess) {
                ToastUtils.showLong(it.errMessage)
            }
            vipPriceAdapter.data = it.listData
//            vipPriceAdapter.notifyDataSetChanged()
            if (vipPriceAdapter.data.size > 0) {
                vipPriceAdapter.selectItem(vipPriceAdapter.getItem(0) as PayConfigBean)
            }
        }
    }

    companion object {

        const val TAG = "MemberSuperFragment"
        fun start(): MemberSuperFragment {
            val fragment = MemberSuperFragment()
            return fragment
        }
    }
}