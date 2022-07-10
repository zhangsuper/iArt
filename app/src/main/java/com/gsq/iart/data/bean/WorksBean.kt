package com.gsq.iart.data.bean

import java.io.Serializable

/**
 * 作品实体类
 */
class WorksBean(var title: String,
                var author: String,
                var url: String,
                var envelopePic: String): Serializable {
}