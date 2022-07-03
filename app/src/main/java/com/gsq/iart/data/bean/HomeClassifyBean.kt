package com.gsq.iart.data.bean

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class HomeClassifyBean(
    val id: Int,
    val name: String
): Serializable
