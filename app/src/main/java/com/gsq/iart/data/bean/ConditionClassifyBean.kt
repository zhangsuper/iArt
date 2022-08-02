package com.gsq.iart.data.bean

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class ConditionClassifyBean(
    var id: Int,
    var name: String,//属性分类名
    var searchField: String,//属性分类搜索时对应的列名
    var subs: List<ConditionClassifyBean>,//属性值列表
    var dataSource: String
): Serializable