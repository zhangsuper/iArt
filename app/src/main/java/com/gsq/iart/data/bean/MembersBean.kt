package com.gsq.iart.data.bean

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class MembersBean(
    val memberType: Int,
    val memberStartDate: String,
    val memberEndDate: String
) : Serializable