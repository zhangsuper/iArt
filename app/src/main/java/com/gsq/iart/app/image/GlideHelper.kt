package com.gsq.iart.app.image

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.request.transition.Transition
import com.gsq.iart.R
import com.gsq.iart.app.ext.retryLoad
import com.gsq.iart.app.ext.showError
import com.gsq.iart.app.ext.showLoading
import com.kingja.loadsir.core.LoadService

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
        showPlaceHolder: Boolean? = true,
        loadService: LoadService<*>? = null,
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
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    loadService?.showError("加载失败")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    loadService?.showSuccess()
                    return false
                }
            })
            .apply(options)
            .into(imageView)
    }

    /**
     * 加载图片并设置动画
     */
    fun loadWithLoading(
        imageView: ImageView,
        loadService: LoadService<*>?,
        realUrl: String?,
        isFullScreen: Boolean = false,
        onFailed: () -> Unit = {},
        onStart: () -> Unit = {},
        onReady: () -> Unit = {}
    ): DrawableImageViewTarget {
        var context = imageView.context
        var glideRequests = GlideApp.with(context)
        var requestOptions =
            RequestOptions.placeholderOf(R.color.transparent)
                .format(DecodeFormat.PREFER_RGB_565)
                .error(R.color.color_A7A7A7)
                // .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
//        if (isFullScreen) {
        // 全屏时不显示动画
        requestOptions.dontAnimate()
//        } else {
//            requestOptions.centerCrop()
//        }
        return glideRequests.load(realUrl)
            .transition(withCrossFade(DrawableCrossFadeFactory.Builder()))
            .apply(
                requestOptions
            ).error(R.color.color_EDF2F7)
            .into(object : NoLeakDrawableImageViewTarget(imageView) {

                override fun onLoadStarted(placeholder: Drawable?) {
                    super.onLoadStarted(placeholder)
//                    mLoadingView?.visible()
//                    mLoadingView?.showAnim()
                    loadService?.showLoading()
                    onStart()
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    super.onResourceReady(resource, transition)
//                    mLoadingView?.removeAnim()
//                    mLoadingView?.gone()
                    loadService?.showSuccess()
                    onReady()
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    retryLoad(imageView, {
                        loadWithLoading(
                            imageView,
                            loadService,
                            realUrl,
                            isFullScreen,
                            onFailed,
                            onStart,
                            onReady
                        )
                    }, {
//                        mLoadingView?.showFailUi()
//                        mLoadingView?.visible()
                        loadService?.showError("加载失败")
                        onFailed()
                    })
                }
            })
    }
}