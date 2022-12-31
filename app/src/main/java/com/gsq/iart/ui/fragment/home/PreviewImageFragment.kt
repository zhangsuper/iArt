package com.gsq.iart.ui.fragment.home

import android.graphics.drawable.Drawable
import android.os.Bundle
import com.airbnb.mvrx.args
import com.airbnb.mvrx.asMavericksArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.gsq.iart.app.App
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.loadServiceInit
import com.gsq.iart.app.ext.showError
import com.gsq.iart.app.ext.showLoading
import com.gsq.iart.data.bean.DetailArgsType
import com.gsq.iart.data.event.BigImageClickEvent
import com.gsq.iart.databinding.FragmentPreviewImageBinding
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.fragment_preview_image.*
import org.greenrobot.eventbus.EventBus
import java.io.File

class PreviewImageFragment : BaseFragment<BaseViewModel, FragmentPreviewImageBinding>() {

    private val args: DetailArgsType by args()

    //界面状态管理者
    private lateinit var loadsir: LoadService<Any>

    companion object {
        fun start(args: DetailArgsType): PreviewImageFragment {
            val fragment = PreviewImageFragment()
            fragment.arguments = args.asMavericksArgs()
            return fragment
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        var hdPics = args.workHdPics
        //状态页配置
        loadsir = loadServiceInit(photo_view) {
            //点击重试时触发的操作
            loadsir.showLoading()
            loadPic()
        }
//        photo_view.scaleType = ImageView.ScaleType.FIT_CENTER
//        photo_view.setOnPhotoTapListener { view, x, y ->
//            EventBus.getDefault().post(BigImageClickEvent(true))
//        }
//        photo_view.setScaleLevels(1.0f, 4.0f, 20.0f)
        photo_view.setOnClickListener {
            EventBus.getDefault().post(BigImageClickEvent(true))
        }
        photo_view.isQuickScaleEnabled = true
        photo_view.isPanEnabled = true
        photo_view.maxScale = 20f
        photo_view.minScale = 0.5f
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        loadsir.showLoading()
        loadPic()
    }

    private fun loadPic() {
//        GlideHelper.load(photo_view, args.workHdPics.url, loadService = loadsir)
//        Glide.with(App.instance)
//            .download(args.workHdPics.url)
//            .into(object : SimpleTarget<File>() {
//                override fun onResourceReady(resource: File, transition: Transition<in File>?) {
//                    loadsir.showSuccess()
//                    ThreadUtils.runOnUiThread {
//                        photo_view?.let {
//                            if (resource?.absolutePath != null) {
//                                it.setImage(ImageSource.uri(resource.absolutePath))
//                            }
//                            // 最大显示比例
//                            it.maxScale = 20f
//                            it.minScale = 0.5f
//                        }
//                    }
//                }
//
//            })

        Glide.with(App.instance).load(args.workHdPics.url)
            .downloadOnly(object : SimpleTarget<File?>() {
                override fun onResourceReady(
                    resource: File,
                    glideAnimation: Transition<in File?>?
                ) {
                    loadsir.showSuccess()
//                    val sWidth = BitmapFactory.decodeFile(resource.absolutePath).width
//                    val sHeight = BitmapFactory.decodeFile(resource.absolutePath).height
//                    val wm = ContextCompat.getSystemService(App.instance, WindowManager::class.java)
//                    val width = wm?.defaultDisplay?.width ?: 0
//                    val height = wm?.defaultDisplay?.height ?: 0
//                    if (sHeight >= height
//                        && sHeight / sWidth >= 3
//                    ) {
//                        photo_view.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
//                        photo_view.setImage(
//                            ImageSource.uri(Uri.fromFile(resource)),
//                            ImageViewState(0.5f, PointF(0f, 0f), 0)
//                        )
//                    } else {
//                        photo_view.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
//                        photo_view.setImage(ImageSource.uri(Uri.fromFile(resource)))
//                        photo_view.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER_IMMEDIATE)
//                    }
                    loadsir.showSuccess()
                    photo_view?.let {
                        if (resource?.absolutePath != null) {
                            it.setImage(ImageSource.uri(resource.absolutePath))
                        }
                        // 最大显示比例
                        it.maxScale = 20f
                        it.minScale = 0.5f
                    }
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    loadsir.showError("加载失败")
                }
            })

    }

    fun getPhotoView(): SubsamplingScaleImageView {
        return photo_view
    }

}