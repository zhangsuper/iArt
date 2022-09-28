package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.image.GlideHelper
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.WxLoginUtil
import com.gsq.iart.data.bean.PayConfigBean
import com.gsq.iart.data.bean.VipPriceBean
import com.gsq.iart.data.event.PayResultEvent
import com.gsq.iart.databinding.FragmentMemberBinding
import com.gsq.iart.ui.adapter.VipPriceAdapter
import com.gsq.iart.viewmodel.LoginViewModel
import com.gsq.iart.viewmodel.MemberViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import kotlinx.android.synthetic.main.fragment_member.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 我的会员
 */
class MemberFragment : BaseFragment<MemberViewModel, FragmentMemberBinding>() {

    private val vipPriceAdapter: VipPriceAdapter by lazy { VipPriceAdapter() }
    private var priceList: MutableList<VipPriceBean> = mutableListOf()
    private var payType = 1 //1:微信支付  2：支付宝支付
    private var mLoginViewModel: LoginViewModel? = null

    override fun initView(savedInstanceState: Bundle?) {
        title_layout.setBackListener {
            nav().navigateUp()

        }
        price_recycler_view.adapter = vipPriceAdapter
        initDate()
        vipPriceAdapter.setOnItemClickListener { adapter, view, position ->
            vipPriceAdapter.selectItem(adapter.getItem(position) as PayConfigBean)
        }
        wechat_pay_view.setOnClickListener {
            payType = 1
            iv_wechat_pay_selected.setImageResource(R.drawable.check_box_selected)
            iv_ali_pay_selected.setImageResource(R.drawable.check_box_unselected)
        }
        ali_pay_view.setOnClickListener {
            payType = 2
            iv_wechat_pay_selected.setImageResource(R.drawable.check_box_unselected)
            iv_ali_pay_selected.setImageResource(R.drawable.check_box_selected)
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
        SpanUtils.with(agreement_privacy)
            .append("我已阅读并同意")
            .append("《${resources.getString(R.string.vip_agreement)}》")
            .setForegroundColor(resources.getColor(R.color.color_EC8E58))
            .setClickSpan(clickableSpan)
            .create()

        pay_button.setOnClickListener {
            vipPriceAdapter.selectBean?.let {
                if (!checkbox.isChecked) {
                    ToastUtils.showShort("请勾选同意《会员服务协议》")
                    return@setOnClickListener
                }
                mViewModel.createPreparePay(it.id)
            } ?: let {
                ToastUtils.showLong("请选择开通时限")
            }
        }
        mLoginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        EventBus.getDefault().register(this)
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        mViewModel.getPayConfig()
        mLoginViewModel?.getUserInfo()
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.payConfigDataState.observe(viewLifecycleOwner) {
            if (!it.isSuccess) {
                ToastUtils.showLong(it.errMessage)
            }
            vipPriceAdapter.data = it.listData
            vipPriceAdapter.notifyDataSetChanged()
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

        mLoginViewModel?.loginResultDataState?.observe(this) {
            if (it.isSuccess) {
                it.data?.let { userInfo ->
                    CacheUtil.setUser(userInfo)
                    updateUserInfo()
                }
            }
        }
    }

    private fun initDate() {
        updateUserInfo()
    }

    private fun updateUserInfo() {
        var userInfo = CacheUtil.getUser()
        nike_name.text = userInfo?.nickname
        GlideHelper.load(iv_avatar, userInfo?.headImgUrl)
        if (userInfo?.memberType == 1) {
            vip_status.text = "国画通会员有效期至${userInfo?.memberEndDate}"
            pay_button.text = "立即续费"
        } else {
            vip_status.text = "您暂未开通国画通会员"
            pay_button.text = "立即开通"
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(event: PayResultEvent?) {
        //服务器端的接收的支付通知
        mLoginViewModel?.getUserInfo()
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


}