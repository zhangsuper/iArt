package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.BuildConfig
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
import com.gsq.mvvm.ext.view.visible
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
        setUserInfo()
    }

    private fun setLoginStatus() {
        if (CacheUtil.isLogin()) {
            nike_name.text = CacheUtil.getUser()?.name
            user_id.text = CacheUtil.getUser()?.id
            GlideHelper.load(iv_avatar, CacheUtil.getUser()?.avatarUrl)
        } else {
            nike_name.text = getString(R.string.app_login_in)
            user_id.text = getString(R.string.app_login_get_vip)
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
            nav().navigateAction(R.id.action_mainFragment_to_memberFragment)

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
            setUserInfo()
        }
    }

    private fun setUserInfo() {
        CacheUtil.getWeChatLoginCode()?.let {
            nike_name.text = it
            if (BuildConfig.DEBUG) {
                wechat_code.setText(it)
                wechat_code.visible()
            } else {
                wechat_code.gone()
            }
        }
    }

}