package com.gsq.iart.data.bean

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class DictionaryMenuBean(
    var id: Int,
    var name: String,//属性分类名
    var searchField: String,//属性分类搜索时对应的列名
    var subs: MutableList<DictionaryMenuBean>,//属性值列表
    var dataSource: String,
    var isSelected: Boolean = false
) : Serializable