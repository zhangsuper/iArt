package com.gsq.iart.data.request

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class CompareRenameRequestParam(
    var id: Long? = null,
    var name: String? = null,
) : Serializable {
}