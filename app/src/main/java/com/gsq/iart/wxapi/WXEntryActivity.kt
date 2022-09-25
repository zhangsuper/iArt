package com.gsq.iart.wxapi

import android.app.Activity
import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.BuildConfig
import com.gsq.iart.data.event.LoginEvent
import com.gsq.iart.data.event.PayResultEvent
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import org.greenrobot.eventbus.EventBus

class WXEntryActivity : Activity(), IWXAPIEventHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var api = WXAPIFactory.createWXAPI(this, BuildConfig.WEIXIN_KEY, true)
        api.handleIntent(intent, this)
        finish()
    }


    override fun onReq(p0: BaseReq?) {
        //微信发送的请求将回调到这
    }

    override fun onResp(baseResp: BaseResp) {
        //发送到微信请求的响应结果将回调到这
        if (baseResp.type == ConstantsAPI.COMMAND_PAY_BY_WX) {
            //微信支付结果回调
            when (baseResp.errCode) {
                BaseResp.ErrCode.ERR_OK -> {
                    EventBus.getDefault().post(PayResultEvent(""))
                }
                BaseResp.ErrCode.ERR_COMM -> {
                    ToastUtils.showLong("支付失败")
                }
                BaseResp.ErrCode.ERR_USER_CANCEL -> {
                    ToastUtils.showLong("用户取消支付")
                }
            }

        } else {
            var result = when (baseResp.errCode) {
                BaseResp.ErrCode.ERR_OK -> {
                    "登录成功"
                    var code = (baseResp as? SendAuth.Resp)?.code
                    code?.let {
                        EventBus.getDefault().post(LoginEvent(it))
                    }
                }
                BaseResp.ErrCode.ERR_AUTH_DENIED -> {
                    "用户拒绝授权"
                    ToastUtils.showLong("授权失败")
                }
                BaseResp.ErrCode.ERR_USER_CANCEL -> {
                    "用户取消"
                    ToastUtils.showLong("取消登录")
                }
                else -> {
                    "失败"
                    ToastUtils.showLong("登录失败")
                }
            }
        }

//        ToastUtils.showLong(result)
    }

}