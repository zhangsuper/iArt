package com.gsq.iart.data.bean

import androidx.annotation.Keep
import java.io.Serializable

/**
 * 作品实体类
 */
@Keep
class WorksBean(
    var age: String,//年到
    var artId: String,//作者id
    var author: String,//作者
    var collectionStampInfo: String,//收藏印
    var collector: String,//收藏家
    var description: String,//简要介绍
    var displayType: Int,//展示方式：1=直接展示；2=横向拼接
    var detailedSubject: String,//子题材
    var downloadCount: Int,//下载次数
    var famousEvaluation: String,//名家评价
    var hdPics: List<WorkHdPics>,//高清图
    var id: String,
    var materialType: String,//颜色
    var mediaType: String,//材质
    var name: String,//作品名称
    var otherPostscriptInfo: String,//鉴赏跋
    var otherPostscriptInfoAuthor: String,//题跋作者
    var owner: String,//现存归属
    var ownerStampInfo: String,//作者印
    var referenceBook: String,//著录书籍
    var selfPostscriptInfo: String,//自跋
    var size: String,//尺寸
    var skilOfPainting: String,//表现技法
    var styleType: String,//形制
    var subject: String,//题材
    var tags: String,//标签
    var thumb: String,//缩略图
    var viewCount: Int,//浏览次数
    var workId: String,
    var workType: String,//作品类型：PIC:单图；COLL:合集
    var isCollect: Int,//0 ,1
    var createdTime: String
) : Serializable {
}

class WorkHdPics(
    var fileSize: String,
    var url: String
) : Serializable
