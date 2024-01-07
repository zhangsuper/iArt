package com.gsq.iart.data.bean

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class DictionarySetsBean(
    val createdTime: String,
    val id: Long,
    val img: String,
    var name: String,
    var num: Int,
    val userId: Long
) : Serializable