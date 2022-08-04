package com.gsq.iart.app.image

import android.widget.ImageView
import com.bumptech.glide.request.target.DrawableImageViewTarget

open class NoLeakDrawableImageViewTarget(imageView: ImageView) : DrawableImageViewTarget(imageView) {

    override fun onDestroy() {
        super.onDestroy()
        request?.clear()
    }

}