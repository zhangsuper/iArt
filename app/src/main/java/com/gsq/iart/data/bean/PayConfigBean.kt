package com.gsq.iart.data.bean

import java.io.Serializable

data class PayConfigBean(
    val id: Long,
    val payAmount: Float,
    val termDesc: String,
    val type: Int
) : Serializable