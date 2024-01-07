package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.bindViewPager2
import com.gsq.iart.app.ext.init
import com.gsq.iart.app.image.GlideHelper
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.MobAgentUtil
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.app.util.WxLoginUtil
import com.gsq.iart.data.bean.DictionaryArgsType
import com.gsq.iart.data.bean.MemberArgsType
import com.gsq.iart.data.bean.PayConfigBean
import com.gsq.iart.data.bean.UserInfo
import com.gsq.iart.data.bean.VipPriceBean
import com.gsq.iart.data.event.PayResultEvent
import com.gsq.iart.databinding.FragmentMemberBinding
import com.gsq.iart.ui.adapter.VipPriceAdapter
import com.gsq.iart.ui.fragment.dictionary.DictionarySubListFragment
import com.gsq.iart.viewmodel.LoginViewModel
import com.gsq.iart.viewmodel.MemberViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.onClick
import kotlinx.android.synthetic.main.fragment_dictionary_list.dictionary_magic_indicator
import kotlinx.android.synthetic.main.fragment_dictionary_list.dictionary_view_pager
import kotlinx.android.synthetic.main.fragment_member.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 我的会员
 */
class MemberFragment : BaseFragment<MemberViewModel, FragmentMemberBinding>() {

    companion object {
        const val INTENT_KEY_TYPE = "data"
        const val INTENT_VALUE_WORKS = "works"
        const val INTENT_VALUE_DICTIONARY = "dictionary"
        const val INTENT_VALUE_SEARCH = "search"
        const val INTENT_VALUE_DOWNLOAD = "download"
        const val INTENT_VALUE_RECHARGE = "recharge"
        const val INTENT_VALUE_RENEW = "renew"
    }

    private val vipPriceAdapter: VipPriceAdapter by lazy { VipPriceAdapter(1) }
    private var priceList: MutableList<VipPriceBean> = mutableListOf()
    private var payType = 1 //1:微信支付  2：支付宝支付
    private var mLoginViewModel: LoginViewModel? = null
    private var agreementType: String? = null

    //标题集合
    var mDataList: ArrayList<String> = arrayListOf()
    //fragment集合
    var fragments: ArrayList<Fragment> = arrayListOf()

    var memberType: Int = 99

    override fun initView(savedInstanceState: Bundle?) {
        title_layout.setBackListener {
            nav().navigateUp()

        }
        agreementType = arguments?.getString(UserAgreementFragment.INTENT_KEY_TYPE)
        agreementType?.let {
            var eventMap = mutableMapOf<String, Any?>()
            eventMap["type"] = it
            MobAgentUtil.onEvent("vip", eventMap)
        }
//        price_recycler_view.adapter = vipPriceAdapter
//        vipPriceAdapter.setOnItemClickListener { adapter, view, position ->
//            vipPriceAdapter.selectItem(adapter.getItem(position) as PayConfigBean)
//        }
//        wechat_pay_view.setOnClickListener {
//            payType = 1
//            iv_wechat_pay_selected.setImageResource(R.drawable.check_box_selected)
//            iv_ali_pay_selected.setImageResource(R.drawable.check_box_unselected)
//        }
//        ali_pay_view.setOnClickListener {
//            payType = 2
//            iv_wechat_pay_selected.setImageResource(R.drawable.check_box_unselected)
//            iv_ali_pay_selected.setImageResource(R.drawable.check_box_selected)
//        }
        user_info_view.onClick {
            if (!CacheUtil.isLogin()) {
                nav().navigateAction(R.id.action_memberFragment_to_loginFragment)
                var eventMap = mutableMapOf<String, Any?>()
                eventMap["type"] = "vip"
                MobAgentUtil.onEvent("signin", eventMap)
            }
        }

//        val clickableSpan = object : ClickableSpan() {
//            override fun onClick(widget: View) {
//                //会员服务协议
//                (widget as TextView).highlightColor =
//                    resources.getColor(android.R.color.transparent)
//                nav().navigateAction(
//                    R.id.action_memberFragment_to_userAgreementFragment,
//                    bundleOf(UserAgreementFragment.INTENT_KEY_TYPE to UserAgreementFragment.INTENT_VALUE_VIP_AGREEMENT)
//                )
//            }
//
//            override fun updateDrawState(ds: TextPaint) {
//                ds.isUnderlineText = false
//            }
//        }
//        SpanUtils.with(agreement_privacy)
//            .append("我已阅读并同意")
//            .append("《${resources.getString(R.string.vip_agreement)}》")
//            .setForegroundColor(resources.getColor(R.color.color_EC8E58))
//            .setClickSpan(clickableSpan)
//            .create()
//
//        pay_button.setOnClickListener {
//            vipPriceAdapter.selectBean?.let {
//                if (!checkbox.isChecked) {
//                    ToastUtils.showShort("请勾选同意《会员服务协议》")
//                    return@setOnClickListener
//                }
//                if (!CacheUtil.isLogin()) {
//                    nav().navigateAction(R.id.action_memberFragment_to_loginFragment)
//                    var eventMap = mutableMapOf<String, Any?>()
//                    eventMap["type"] = "vip"
//                    MobAgentUtil.onEvent("signin", eventMap)
//                    return@setOnClickListener
//                }
//                mViewModel.createPreparePay(it.id)
//                var eventMap = mutableMapOf<String, Any?>()
//                eventMap["payType"] = "wechat"
//                MobAgentUtil.onEvent("vip_buy", eventMap)
//            } ?: let {
//                ToastUtils.showLong("请选择开通套餐")
//            }
//        }
        mLoginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        EventBus.getDefault().register(this)


        mDataList.add("超级会员")
        mDataList.add("国画通会员")

        fragments.add(
            MemberSuperFragment.start()
        )
        fragments.add(
            MemberGhtFragment.start()
        )

        //初始化viewpager2
        mViewBind.memberViewPager.init(this, fragments)
        //初始化 magic_indicator
        mViewBind.memberMagicIndicator.bindViewPager2(mViewBind.memberViewPager, mDataList)
        mViewBind.memberViewPager.offscreenPageLimit = 2
        mViewBind.memberViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if(position == 0){
                    memberType = 99
                }else if(position == 1){
                    memberType = 1
                }
                updateMemberInfo()
                super.onPageSelected(position)
            }
        })

        if(agreementType == INTENT_VALUE_WORKS){
            mViewBind.memberViewPager.setCurrentItem(1, false)
        }else{
            mViewBind.memberViewPager.setCurrentItem(0, false)
        }
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        if (CacheUtil.isLogin()) {
            mLoginViewModel?.getUserInfo()
        }
//        mViewModel.getPayConfig(1)
    }

    override fun createObserver() {
        super.createObserver()
//        mViewModel.payConfigDataState.observe(viewLifecycleOwner) {
//            if (!it.isSuccess) {
//                ToastUtils.showLong(it.errMessage)
//            }
//            vipPriceAdapter.data = it.listData
////            vipPriceAdapter.notifyDataSetChanged()
//            if (vipPriceAdapter.data.size > 0) {
//                vipPriceAdapter.selectItem(vipPriceAdapter.getItem(0) as PayConfigBean)
//            }
//        }
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

    override fun onResume() {
        super.onResume()
        StatusBarUtil.init(
            requireActivity(),
            fitSystem = true,
            statusBarColor = R.color.white,
            isDarkFont = true
        )
        initDate()
    }

    private fun initDate() {
        updateUserInfo()
    }

    private fun updateUserInfo() {
        var userInfo = CacheUtil.getUser()
        nike_name.text = userInfo?.nickname
        GlideHelper.load(iv_avatar, userInfo?.headImgUrl, R.drawable.icon_user_default)
        updateMemberInfo()
    }

    private fun updateMemberInfo(){
        var userInfo = CacheUtil.getUser()
        if(memberType == 99){
            //超级会员
            var memberBean = userInfo?.members?.find { it.memberType == 99 }
            if(memberBean != null){
                vip_status.text = "超级会员有效期至${memberBean.memberEndDate}"
                pay_button.text = "立即续费"
            }else{
                vip_status.text = "您暂未开通超级会员"
                pay_button.text = "立即开通"
            }
            mViewBind.userInfoView.setBackgroundResource(R.drawable.bg_f6cf88_ffecc7_conner_10)
        }else if(memberType == 1){
            //国画通会员
            var memberBean = userInfo?.members?.find { it.memberType == 1 }
            if(memberBean != null){
                vip_status.text = "国画通会员有效期至${memberBean.memberEndDate}"
                pay_button.text = "立即续费"
            }else{
                vip_status.text = "您暂未开通国画通会员"
                pay_button.text = "立即开通"
            }
            mViewBind.userInfoView.setBackgroundResource(R.drawable.bg_de824e_faba97_conner_10)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: PayResultEvent?) {
        //服务器端的接收的支付通知
        LogUtils.d("PayResultEvent：getUserInfo")
        mLoginViewModel?.getUserInfo()
        var eventMap = mutableMapOf<String, Any?>()
        eventMap["payType"] = "wechat"
        if (event?.code == "true") {
            MobAgentUtil.onEvent("paid_suc", eventMap)
        } else if (event?.code == "false") {
            eventMap["reason"] = event.msg
            MobAgentUtil.onEvent("paid_fail", eventMap)
        }
    }


    override fun onDestroy() {
        agreementType?.let {
            var eventMap = mutableMapOf<String, Any?>()
            eventMap["type"] = it
            MobAgentUtil.onEvent("vip_cancel", eventMap)
        }
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


}