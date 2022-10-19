package com.gsq.iart.data

import android.os.Environment
import com.gsq.iart.app.util.FileUtil
import java.io.File

object Constant {

    //glide缓存目录和缓存池大小
    const val GLIDE_CACHE_DIR = "AppGlide"
    const val GLIDE_CACHE_SIZE = 500 * 1024 * 1024
    // 是否允许图片加载失败重试一次
    const val ENABLE_IMAGE_AUTO_RETRY = true
    // 封面图加载失败重试等待时间(毫秒）
    const val LOAD_PIC_DELAY = 200L

    const val DEFAULT_REQUEST_SIZE = 10//每页大小

    const val COMPLEX_TYPE_GROUP = "type_group"//分组
    const val COMPLEX_TYPE_SEARCH = "type_search"//搜索
    const val COMPLEX_TYPE_COLLECT = "type_collect"//收藏

    const val WORKS_SUB_TYPE_HOT = 0//热门
    const val WORKS_SUB_TYPE_NEW = 1//新上

    const val DATA_WORK = "data_work"
    const val INTENT_TYPE = "intent_type"
//    var DOWNLOAD_PARENT_PATH = App.instance.getExternalFilesDir(null)?.absolutePath + File.separator + "download"
    var DOWNLOAD_PARENT_PATH = Environment.getExternalStorageDirectory().absolutePath + File.separator + "yishuguan"

    const val CLIENT_PATH = "yishuguan" //SD卡根目录存储位置
    const val DOWNLOAD_PATH = "/download/"//下载目录

    val download_path: String = FileUtil.getSavePath(CLIENT_PATH + DOWNLOAD_PATH)


}