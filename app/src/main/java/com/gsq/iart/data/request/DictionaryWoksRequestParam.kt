package com.gsq.iart.data.request

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class DictionaryWoksRequestParam(
    var pageNum: Int,//页码
    var pageSize: Int,//每页大小
    var searchKey: String? = null,//关键词搜索
    var tag1: String? = null,
    var tag2: String? = null,
    var tag3: String? = null,
    var tag4: String? = null,
) : Serializable {
}