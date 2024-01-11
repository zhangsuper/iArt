package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import androidx.core.os.bundleOf
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.BuildConfig
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.image.GlideHelper
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.MobAgentUtil
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.databinding.FragmentMineBinding
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.onClick
import com.gsq.mvvm.ext.view.visible
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 * 我的页面
 */
class MineFragment : BaseFragment<BaseViewModel, FragmentMineBinding>() {

    override fun onResume() {
        super.onResume()
        StatusBarUtil.init(requireActivity(), statusBarColor = R.color.color_EFF1F5)
        setLoginStatus()
    }

    override fun getUserVisibleHint(): Boolean {
        return super.getUserVisibleHint()
    }

    override fun onPause() {
        super.onPause()
        StatusBarUtil.init(requireActivity(), statusBarColor = R.color.white)
    }

    override fun initView(savedInstanceState: Bundle?) {
        item_dictionary_list.onClick {
            //我的图单
            MobAgentUtil.onEvent("mytudan")
            if (CacheUtil.isLogin()) {
                nav().navigateAction(R.id.action_mainFragment_to_myDictionarySetsFragment)
            } else {
                //跳转登录界面
                nav().navigateAction(R.id.action_mainFragment_to_loginFragment)
                var eventMap = mutableMapOf<String, Any?>()
                eventMap["type"] = "signin"
                MobAgentUtil.onEvent("signin", eventMap)
            }
        }
        item_collect.onClick {
            //收藏
            MobAgentUtil.onEvent("mycollect")
            if (CacheUtil.isLogin()) {
                nav().navigateAction(R.id.action_mainFragment_to_myCollectFragment)
            } else {
                //跳转登录界面
                nav().navigateAction(R.id.action_mainFragment_to_loginFragment)
                var eventMap = mutableMapOf<String, Any?>()
                eventMap["type"] = "signin"
                MobAgentUtil.onEvent("signin", eventMap)
            }
        }
        item_setting.onClick {
            //设置
            nav().navigateAction(R.id.action_mainFragment_to_settingFragment)
        }
        item_contact_QQ.setRightMsgListener {
            //复制客服QQ
            ClipboardUtils.copyText(item_contact_QQ.getRightSubDescTv().text.toString())
//            item_contact.context.copyToClipboard(item_contact.getRightSubDescTv().text.toString())
            ToastUtils.showShort("已复制到粘贴板")
            MobAgentUtil.onEvent("customer_qq")
        }
        item_contact_wechat.setRightMsgListener {
            //复制客服微信
            ClipboardUtils.copyText(item_contact_wechat.getRightSubDescTv().text.toString())
//            item_contact.context.copyToClipboard(item_contact.getRightSubDescTv().text.toString())
            ToastUtils.showShort("已复制到粘贴板")
            MobAgentUtil.onEvent("customer_wechat")
        }
        setListener()
        setLoginStatus()
//        setUserInfo()
        tv_version.text = "V${BuildConfig.VERSION_NAME}"
    }

    private fun setLoginStatus() {
        if (CacheUtil.isLogin()) {
            nike_name.text = CacheUtil.getUser()?.nickname
            user_id.text = "ID:${CacheUtil.getUser()?.userId}"
            GlideHelper.load(iv_avatar, CacheUtil.getUser()?.headImgUrl)
            if(CacheUtil.getUser()?.members?.size?:0>0){
                var superMember = CacheUtil.getUser()?.members?.find { it.memberType ==99 }
                if(superMember!=null){
                    expired_time.text = "超级会员${superMember.memberEndDate}到期"

                }else{
                    expired_time.text = "国通画${CacheUtil.getUser()?.members?.get(0)?.memberEndDate}到期"
                }
                expired_time.visible()
                join_vip_btn.text = "立即续费"
            }else{
                expired_time.gone()
                join_vip_btn.text = "立即开通"
            }
        } else {
            nike_name.text = getString(R.string.app_login_in)
            user_id.text = getString(R.string.app_login_get_vip)
            iv_avatar.setImageResource(R.drawable.icon_user_default)
            expired_time.gone()
            join_vip_btn.text = "立即开通"
        }
    }

    private fun setListener() {
        user_info_view.setOnClickListener {
            if (CacheUtil.isLogin()) {

            } else {
                //跳转登录界面
                nav().navigateAction(R.id.action_mainFragment_to_loginFragment)
                var eventMap = mutableMapOf<String, Any?>()
                eventMap["type"] = "signin"
                MobAgentUtil.onEvent("signin", eventMap)
            }
        }
        join_vip_btn.setOnClickListener {
            //我的会员
//            if (CacheUtil.isLogin()) {
//                nav().navigateAction(R.id.action_mainFragment_to_memberFragment)
//            } else {
//                //跳转登录界面
//                nav().navigateAction(R.id.action_mainFragment_to_loginFragment)
//            }
            if (CacheUtil.getUserVipStatus() != 0) {
                nav().navigateAction(
                    R.id.action_mainFragment_to_memberFragment,
                    bundleOf(MemberFragment.INTENT_KEY_TYPE to MemberFragment.INTENT_VALUE_RENEW)
                )
            } else {
                nav().navigateAction(
                    R.id.action_mainFragment_to_memberFragment,
                    bundleOf(MemberFragment.INTENT_KEY_TYPE to MemberFragment.INTENT_VALUE_RECHARGE)
                )
            }
        }
    }


    override fun createObserver() {
        super.createObserver()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            StatusBarUtil.init(requireActivity())
        } else {
            StatusBarUtil.init(requireActivity(), statusBarColor = R.color.color_EFF1F5)
            setLoginStatus()
        }
    }

}