package com.gsq.iart.app.image

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class ImageBlurTransformation constructor(context: Context, url: String?): BitmapTransformation() {

    private var context: Context? = null
    var url: String? = null
    private val BLUR_MAX_SIZE = 60

    init {
        this.context = context.applicationContext
        this.url = url
    }

    override fun equals(other: Any?): Boolean {
        return if (this === other) true else url == (other as ImageBlurTransformation).url
    }


    override fun hashCode(): Int {
        return url.hashCode()
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap? {
        return blurBitmap(context, toTransform, 10f, outWidth, outHeight)
    }

    /**
     * @param context   上下文对象
     * @param image     需要模糊的图片
     * @param outWidth  输入出的宽度
     * @param outHeight 输出的高度
     * @return 模糊处理后的Bitmap
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun blurBitmap(
        context: Context?,
        image: Bitmap?,
        blurRadius: Float,
        outWidth: Int,
        outHeight: Int
    ): Bitmap? {
        var destWidth = 0
        var destHeight = 0
        if (outWidth <= BLUR_MAX_SIZE || outHeight <= BLUR_MAX_SIZE) {
            destWidth = outWidth
            destHeight = outHeight
        } else {
            val widthScale = (outWidth / BLUR_MAX_SIZE).toFloat()
            val heightScale = (outHeight / BLUR_MAX_SIZE).toFloat()
            val scale = Math.min(widthScale, heightScale)
            destWidth = (outWidth / scale).toInt()
            destHeight = (outHeight / scale).toInt()
        }
        // 将缩小后的图片做为预渲染的图片
        val inputBitmap = Bitmap.createScaledBitmap(image!!, destWidth, destHeight, false)
        // 创建一张渲染后的输出图片
        val outputBitmap = Bitmap.createBitmap(inputBitmap)

        // 创建RenderScript内核对象
        val rs = RenderScript.create(context!!.applicationContext)
        // 创建一个模糊效果的RenderScript的工具对象
        val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间
        // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去
        val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
        val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
        // 设置渲染的模糊程度, 25f是最大模糊度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            blurScript.setRadius(blurRadius)
        }
        // 设置blurScript对象的输入内存
        blurScript.setInput(tmpIn)
        // 将输出数据保存到输出内存中
        blurScript.forEach(tmpOut)
        // 将数据填充到Allocation中
        tmpOut.copyTo(outputBitmap)
        inputBitmap.recycle()
        tmpIn.destroy()
        tmpOut.destroy()
        blurScript.destroy()
        rs.destroy()
        val canvas = Canvas(outputBitmap)
        canvas.drawColor(Color.parseColor("#90000000"))
        return outputBitmap
    }
}