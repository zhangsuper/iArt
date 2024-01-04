package com.gsq.iart.data.bean

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class UserInfo(
    val headImgUrl: String,
    val memberEndDate: String,
    val memberStartDate: String,
    val memberType: Int,//会员类型：0=普通用户，1=国画通
    val members: ArrayList<MembersBean>,//数组表示已经开通的会员(memberType:1=国画通会员，99=超级会员)
    val nickname: String,
    val openid: String,
    val token: String,
    val userId: String
) : Serializable