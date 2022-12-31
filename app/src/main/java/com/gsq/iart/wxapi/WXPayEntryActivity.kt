package com.gsq.iart.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.BuildConfig
import com.gsq.iart.data.event.PayResultEvent
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelpay.PayResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import org.greenrobot.eventbus.EventBus


class WXPayEntryActivity : Activity(), IWXAPIEventHandler {

    private lateinit var api: IWXAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = WXAPIFactory.createWXAPI(this, BuildConfig.WEIXIN_KEY, true)
        api.handleIntent(intent, this)
    }


    override fun onReq(p0: BaseReq?) {
        //微信发送的请求将回调到这
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        api?.handleIntent(intent, this)
    }

    override fun onResp(baseResp: BaseResp) {
        //发送到微信请求的响应结果将回调到这
        // 接收传过来的参数
        val resp = baseResp as PayResp
        val order_id = resp.extData

        // 根据状态去做相应的处理
        if (baseResp.type == ConstantsAPI.COMMAND_PAY_BY_WX) {
            //微信支付结果回调
            when (baseResp.errCode) {
                BaseResp.ErrCode.ERR_OK -> {
                    LogUtils.d("pay success")
                    EventBus.getDefault().post(PayResultEvent("true"))
                }
                BaseResp.ErrCode.ERR_COMM -> {
                    LogUtils.d("pay error:${baseResp.errStr}")
                    ToastUtils.showLong("支付失败")
                    EventBus.getDefault().post(PayResultEvent("false", baseResp.errStr))
                }
                BaseResp.ErrCode.ERR_USER_CANCEL -> {
                    LogUtils.d("pay cancel")
                    ToastUtils.showLong("用户取消支付")
                    EventBus.getDefault().post(PayResultEvent("false", "pay cancel"))
                }
            }
            finish()
        }
    }

}