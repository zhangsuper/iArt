package com.gsq.iart.data.bean

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class DictionaryArgsType constructor(
    val firstTag: String? = null,
    val tag: String? = null,
    val pid: Int? = null,
    val searchKey: String? = null//搜索的key
) : Serializable