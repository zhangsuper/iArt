package com.gsq.iart.data.request

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class WorkPageRequestParam(
    var classifyId: Int,//分类id（国画、壁画）
    var orderType: Int,//排序： 0=热门；1=最新
    var pageNum: Int,//页码
    var pageSize: Int,//每页大小
    var propSearchFiled: String,//属性分类搜索的字段名
    var propSearchValue: String,//属性分类搜索的字段值
    var searchKey: String//关键词搜索
): Serializable {
}