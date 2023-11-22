package com.gsq.iart.data.bean

import androidx.annotation.Keep

@Keep
data class DictionarySetsBean(
    val createdTime: String,
    val id: Int,
    val img: String,
    val name: String,
    val num: Int,
    val userId: Int
)