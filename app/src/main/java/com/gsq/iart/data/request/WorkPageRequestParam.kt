package com.gsq.iart.data.request

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class WorkPageRequestParam(
    var classifyId: Int? = null,//分类id（国画、壁画）
    var orderType: Int? = null,//排序： 0=热门；1=最新
    var pageNum: Int,//页码
    var pageSize: Int,//每页大小
    var propSearches: MutableList<WorkPropSearchBean>? = null,//属性分类搜索的字段名,字段值
    var searchKey: String? = null//关键词搜索
) : Serializable {
}