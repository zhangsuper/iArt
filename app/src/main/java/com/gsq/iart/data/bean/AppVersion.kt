package com.gsq.iart.data.bean

data class AppVersion(
    val desc: String,
    val forceUpdate: Int,
    val url: String,
    val version: String
)