package com.gsq.iart.ui.fragment.home

import android.os.Bundle
import android.widget.ImageView
import com.airbnb.mvrx.args
import com.airbnb.mvrx.asMavericksArgs
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.loadServiceInit
import com.gsq.iart.app.ext.showLoading
import com.gsq.iart.app.image.GlideHelper
import com.gsq.iart.data.bean.DetailArgsType
import com.gsq.iart.data.event.BigImageClickEvent
import com.gsq.iart.databinding.FragmentPreviewImageBinding
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.fragment_preview_image.*
import org.greenrobot.eventbus.EventBus

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
        photo_view.scaleType = ImageView.ScaleType.FIT_CENTER
        photo_view.setOnPhotoTapListener { view, x, y ->
            EventBus.getDefault().post(BigImageClickEvent(true))
        }

    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        loadsir.showLoading()
        loadPic()
    }

    private fun loadPic() {
//        imageView.setImage(ImageSource.uri(args.workHdPics.url))
        GlideHelper.loadWithLoading(photo_view, loadsir, args.workHdPics.url)

//        Glide.with(App.instance)
//            .download(args.workHdPics.url)
//            .into(object : SimpleTarget<File>() {
//                override fun onResourceReady(resource: File, transition: Transition<in File>?) {
//                    ThreadUtils.runOnUiThread {
//                        imageView?.let {
//                            if (resource?.absolutePath != null) {
//                                it.setImage(ImageSource.uri(resource.absolutePath))
//                            }
//                            // 最大显示比例
//                            it.maxScale = 10f
//                            it.minScale = 0.5f
//                        }
//                    }
//                }
//
//            })

    }

}