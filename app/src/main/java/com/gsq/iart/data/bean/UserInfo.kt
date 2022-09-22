package com.gsq.iart.data.bean

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class UserInfo(
    val headImgUrl: String,
    val memberEndDate: String,
    val memberStartDate: String,
    val memberType: Int,
    val nickname: String,
    val openid: String,
    val token: String
) : Serializable