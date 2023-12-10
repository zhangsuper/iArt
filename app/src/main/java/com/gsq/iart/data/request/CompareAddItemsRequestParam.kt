package com.gsq.iart.data.request

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class CompareAddItemsRequestParam(
    var id: Long? = null,
    var workIds: MutableList<Int>? = null,// 图典id
) : Serializable {
}