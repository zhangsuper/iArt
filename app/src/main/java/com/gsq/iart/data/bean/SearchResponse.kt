package com.gsq.iart.data.bean

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

/**
 * 搜索热词
 */
@SuppressLint("ParcelCreator")
@Parcelize
@Keep
data class SearchResponse(var id: Int,
                          var link: String,
                          var name: String,
                          var order: Int,
                          var visible: Int) : Parcelable
