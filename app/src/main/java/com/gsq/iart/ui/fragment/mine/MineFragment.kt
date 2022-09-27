package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.image.GlideHelper
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.databinding.FragmentMineBinding
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.gone
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 * 我的页面
 */
class MineFragment : BaseFragment<BaseViewModel, FragmentMineBinding>() {

    override fun onResume() {
        super.onResume()
        StatusBarUtil.init(requireActivity(), statusBarColor = R.color.color_EFF1F5)
    }

    override fun onPause() {
        super.onPause()
        StatusBarUtil.init(requireActivity(), statusBarColor = R.color.white)
    }

    override fun initView(savedInstanceState: Bundle?) {
        item_collect.setOnClickListener {
            //收藏
            nav().navigateAction(R.id.action_mainFragment_to_myCollectFragment)
        }
        item_setting.setOnClickListener {
            //设置
            nav().navigateAction(R.id.action_mainFragment_to_settingFragment)
        }
        item_contact.setRightMsgListener {
            //复制客服QQ
            ClipboardUtils.copyText(item_contact.getRightSubDescTv().text.toString())
//            item_contact.context.copyToClipboard(item_contact.getRightSubDescTv().text.toString())
            ToastUtils.showShort("已复制到粘贴板")
        }
        setListener()
        setLoginStatus()
//        setUserInfo()
    }

    private fun setLoginStatus() {
        if (CacheUtil.isLogin()) {
            nike_name.text = CacheUtil.getUser()?.nickname
            user_id.text = CacheUtil.getUser()?.openid
            GlideHelper.load(iv_avatar, CacheUtil.getUser()?.headImgUrl)
            if (CacheUtil.getUser()?.memberType == 1) {
                expired_time.text = "国通画${CacheUtil.getUser()?.memberEndDate}到期"
                join_vip_btn.text = "立即续费"
            } else {
                expired_time.gone()
                join_vip_btn.text = "立即开通"
            }
        } else {
            nike_name.text = getString(R.string.app_login_in)
            user_id.text = getString(R.string.app_login_get_vip)
            iv_avatar.setImageResource(R.drawable.icon_avatar_default)
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
            }
        }
        join_vip_btn.setOnClickListener {
            //我的会员
            if (CacheUtil.isLogin()) {
                nav().navigateAction(R.id.action_mainFragment_to_memberFragment)
            } else {
                //跳转登录界面
                nav().navigateAction(R.id.action_mainFragment_to_loginFragment)
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