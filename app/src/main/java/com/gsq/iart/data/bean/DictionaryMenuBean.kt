package com.gsq.iart.data.bean

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class DictionaryMenuBean(
    var id: Int,
    var level: String,
    var name: String,
    var subs: MutableList<DictionaryMenuBean>,//属性值列表
    var icon: String
) : Serializable