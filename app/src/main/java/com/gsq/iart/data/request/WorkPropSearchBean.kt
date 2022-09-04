package com.gsq.iart.data.request

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class WorkPropSearchBean(
    val field: String,
    val value: String
): Serializable
