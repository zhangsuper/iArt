package com.gsq.iart.data.bean

import androidx.annotation.Keep
import java.io.Serializable

/**
 * 图典作品实体类
 */
@Keep
data class DictionaryWorksBean(
    val downloadCount: Int,
    val id: Int,
    val image: String,
    val mainAge: String,
    val mainImage: String,
    val mainName: String,
    val mainWorkId: String,
    val name: String,
    val pay: Int,
    val posX: Int,
    val posY: Int,
    val sort: Int,
    val tag1: String,
    val tag2: String,
    val tag3: String,
    val tag4: String,
    val thumb: String,
    val thumbHeight: Int,
    val thumbWidth: Int,
    val viewCount: Int,
    val workId: String
): Serializable {

}