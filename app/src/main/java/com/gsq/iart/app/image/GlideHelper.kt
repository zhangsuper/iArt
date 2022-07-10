package com.gsq.iart.app.image

import android.annotation.SuppressLint
import android.widget.ImageView
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.request.RequestOptions
import com.gsq.iart.R

/**
 * 图片加载
 */
object GlideHelper {

    /**
     * 可加载网络图片、手机本地图片和视频
     */
    @SuppressLint("CheckResult")
    fun load(
        imageView: ImageView?,
        url: String?,
        defaultRes: Int? = null,
        needBlur: Boolean = false,
        needCache: Boolean = true,
        showPlaceHolder: Boolean? = true
    ) {
        if (imageView == null) {
            return
        }
        val context = imageView.context
//        val realUrl = getRealImageUrl(url, width, height)
        val realUrl = url
        val options = RequestOptions()
        if (defaultRes == null) {
            if (needBlur) {
                val bitmapTransformation: BitmapTransformation =
                    ImageBlurTransformation(context, realUrl)
                options.placeholder(R.color.color_000000)
                    .error(R.color.color_000000)
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .transform(bitmapTransformation)
            } else {
                if (showPlaceHolder == true) {
                    options.placeholder(R.color.color_A7A7A7)
                        .error(R.color.color_A7A7A7)
                        .format(DecodeFormat.PREFER_RGB_565)
                } else {
                    options.error(R.color.color_A7A7A7)
                        .format(DecodeFormat.PREFER_RGB_565)
                }
            }
        } else {
            options.placeholder(defaultRes)
                .error(defaultRes)
        }
        if (needCache) {
            options.diskCacheStrategy(DiskCacheStrategy.DATA)
        } else {
            options.diskCacheStrategy(DiskCacheStrategy.NONE)
        }
        options.dontAnimate()

        GlideApp.with(context)
            .load(realUrl)
            .apply(options)
            .into(imageView)
    }
}