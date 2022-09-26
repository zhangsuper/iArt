package com.gsq.iart.data.bean

data class PayOrderBean(
    val appId: String,
    val partnerId: String,
    val nonceStr: String,
    val prepayId: String,
    val sign: String,
    val tradeType: String,
    val timeStamp: String
)