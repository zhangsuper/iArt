package com.gsq.iart.data.bean

import java.io.Serializable

data class PayConfigBean(
    val id: Int,
    val payAmount: Int,
    val termDesc: String,
    val type: Int
) : Serializable