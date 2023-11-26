package com.gsq.iart.data.request

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class ComparePageRequestParam(
    var pageNum: Int,//页码
    var pageSize: Int,//每页大小
) : Serializable
