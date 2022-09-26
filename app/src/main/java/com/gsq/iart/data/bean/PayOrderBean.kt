package com.gsq.iart.data.bean

data class PayOrderBean(
    val appid: String,
    val mchId: String,
    val nonceStr: String,
    val prepayId: String,
    val sign: String,
    val tradeType: String
)