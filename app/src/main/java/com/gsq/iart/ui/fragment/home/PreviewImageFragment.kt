package com.gsq.iart.ui.fragment.home

import android.os.Bundle
import com.airbnb.mvrx.args
import com.airbnb.mvrx.asMavericksArgs
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.image.GlideHelper
import com.gsq.iart.data.bean.DetailArgsType
import com.gsq.iart.databinding.FragmentPreviewImageBinding
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import kotlinx.android.synthetic.main.fragment_preview_image.*

class PreviewImageFragment :BaseFragment<BaseViewModel, FragmentPreviewImageBinding>() {

    private val args: DetailArgsType by args()

    companion object {
        fun start(args: DetailArgsType): PreviewImageFragment {
            val fragment = PreviewImageFragment()
            fragment.arguments = args.asMavericksArgs()
            return fragment
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        var hdPics = args.workHdPics
        GlideHelper.load(photo_view,hdPics.url)
    }
}