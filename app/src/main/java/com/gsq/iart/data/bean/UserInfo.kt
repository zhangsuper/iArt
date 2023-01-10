package com.gsq.iart.data.bean

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class UserInfo(
    val headImgUrl: String,
    val memberEndDate: String,
    val memberStartDate: String,
    val memberType: Int,//会员类型：0=普通用户，1=国画通
    val nickname: String,
    val openid: String,
    val token: String,
    val userId: String
) : Serializable