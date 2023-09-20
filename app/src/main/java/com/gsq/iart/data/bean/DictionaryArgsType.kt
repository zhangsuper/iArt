package com.gsq.iart.data.bean

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class DictionaryArgsType constructor(
    val tag: String? = null,
    val searchKey: String? = null//搜索的key
) : Serializable