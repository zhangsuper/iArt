package com.gsq.iart.ui.fragment.home

import android.os.Bundle
import com.airbnb.mvrx.args
import com.airbnb.mvrx.asMavericksArgs
import com.blankj.utilcode.util.ThreadUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.gsq.iart.app.App
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.loadServiceInit
import com.gsq.iart.app.ext.showLoading
import com.gsq.iart.data.bean.DetailArgsType
import com.gsq.iart.databinding.FragmentPreviewImageBinding
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.fragment_preview_image.*
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
        loadsir = loadServiceInit(imageView) {
            //点击重试时触发的操作
//            loadsir.showLoading()
            loadPic()
        }
        loadPic()
//        photo_view.scaleType = ImageView.ScaleType.FIT_CENTER
    }

    private fun loadPic() {
        loadsir.showLoading()
//        imageView.setImage(ImageSource.uri(args.workHdPics.url))
//        GlideHelper.loadWithLoading(photo_view,loadsir,args.workHdPics.url)

        Glide.with(App.instance)
            .download(args.workHdPics.url)
            .into(object : SimpleTarget<File>() {
                override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                    ThreadUtils.runOnUiThread {
                        imageView?.let {
                            if (resource?.absolutePath != null) {
                                it.setImage(ImageSource.uri(resource.absolutePath))
                            }
                            // 最大显示比例
                            it.maxScale = 10f
                            it.minScale = 0.5f
                        }
                    }
                }

            })

    }
}