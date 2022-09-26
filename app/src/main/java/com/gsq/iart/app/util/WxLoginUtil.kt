package com.gsq.iart.app.util

import android.content.Context
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.BuildConfig
import com.gsq.iart.data.bean.PayOrderBean
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory


object WxLoginUtil {

    var api: IWXAPI? = null
    var mContext: Context? = null


    fun initWx(context: Context) {
        mContext = context;
        api = WXAPIFactory.createWXAPI(context, BuildConfig.WEIXIN_KEY, true);
        api?.registerApp(BuildConfig.WEIXIN_KEY);

        var msgApi = WXAPIFactory.createWXAPI(context, null)
        // 将该app注册到微信
        msgApi.registerApp(BuildConfig.WEIXIN_KEY)

    }

    /**
     * 微信登录
     */
    fun loginWeChat() {
        if (mContext == null) {
            ToastUtils.showLong("未初始化")
            return
        }
        if (api?.isWXAppInstalled != true) {
            ToastUtils.showLong("未安装微信客户端")
            return
        }
        var req = SendAuth.Req()
        req.scope = BuildConfig.WEIXIN_SCOPE
        req.state = BuildConfig.WEIXIN_STATE
        api?.sendReq(req)
    }

    fun payWithWeChat(bean: PayOrderBean) {
        if (mContext == null) {
            ToastUtils.showLong("未初始化")
            return
        }
        if (api?.isWXAppInstalled != true) {
            ToastUtils.showLong("未安装微信客户端")
            return
        }
        var request = PayReq()
        request.appId = bean.appId
        request.partnerId = bean.partnerId//商户Id
        request.prepayId = bean.prepayId
        request.packageValue = "Sign=WXPay"
        request.nonceStr = bean.nonceStr
        request.timeStamp = bean.timeStamp
        request.sign = bean.sign
        api?.sendReq(request)
    }
}