package com.gsq.iart.data.bean

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class ArgsType constructor(
    val complexType: String,//跳转页面的类型
    val classifyId: Int? = null//分类ID
) : Serializable