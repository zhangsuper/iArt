package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.MobAgentUtil
import com.gsq.iart.app.util.WxLoginUtil
import com.gsq.iart.data.bean.PayConfigBean
import com.gsq.iart.databinding.FragmentMemberGhtBinding
import com.gsq.iart.ui.adapter.VipPriceAdapter
import com.gsq.iart.viewmodel.MemberViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.onClick

class MemberGhtFragment: BaseFragment<MemberViewModel, FragmentMemberGhtBinding>() {

    private val vipPriceAdapter: VipPriceAdapter by lazy { VipPriceAdapter(1) }

    override fun initView(savedInstanceState: Bundle?) {
        mViewBind.priceRecyclerView.adapter = vipPriceAdapter
        vipPriceAdapter.setOnItemClickListener { adapter, view, position ->
            vipPriceAdapter.selectItem(adapter.getItem(position) as PayConfigBean)
        }

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                //会员服务协议
                (widget as TextView).highlightColor =
                    resources.getColor(android.R.color.transparent)
                nav().navigateAction(
                    R.id.action_memberFragment_to_userAgreementFragment,
                    bundleOf(UserAgreementFragment.INTENT_KEY_TYPE to UserAgreementFragment.INTENT_VALUE_VIP_AGREEMENT)
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        SpanUtils.with(mViewBind.agreementPrivacy)
            .append("我已阅读并同意")
            .append("《${resources.getString(R.string.vip_agreement)}》")
            .setForegroundColor(resources.getColor(R.color.color_EC8E58))
            .setClickSpan(clickableSpan)
            .create()

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
        mViewModel.getPayConfig(1)
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
        mViewModel.preparePayDataState.observe(viewLifecycleOwner) {
            if (it.isSuccess) {
                it.data?.let {
                    WxLoginUtil.payWithWeChat(it)
                }
            } else {
                ToastUtils.showLong(it.errorMsg)
            }
        }
    }

    companion object {

        const val TAG = "MemberGhtFragment"
        fun start(): MemberGhtFragment {
            val fragment = MemberGhtFragment()
            return fragment
        }
    }
}