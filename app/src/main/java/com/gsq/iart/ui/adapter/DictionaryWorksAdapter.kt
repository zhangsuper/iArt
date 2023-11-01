package com.gsq.iart.ui.adapter

import android.widget.ImageView
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.gsq.iart.R
import com.gsq.iart.app.ext.setAdapterAnimation
import com.gsq.iart.app.ext.setImageViewRatio
import com.gsq.iart.app.image.GlideHelper
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.SettingUtil
import com.gsq.iart.data.bean.DictionaryMenuBean
import com.gsq.iart.data.bean.WorksBean
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.onClick
import com.gsq.mvvm.ext.view.visible

class DictionaryWorksAdapter constructor(var listener: CallBackListener) :
    BaseQuickAdapter<WorksBean, BaseViewHolder>(R.layout.item_dictionary_works_layout) {

    var mClickContrastListener: ((position: Int) -> Unit)? = null

    fun setClickContrastListener(listener: ((position: Int) -> Unit)?){
        mClickContrastListener = listener
    }

    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }

    interface CallBackListener {
        fun loadMore()
    }

    override fun convert(holder: BaseViewHolder, item: WorksBean) {
        LogUtils.dTag("WorksAdapter", "layoutPosition:${holder.layoutPosition}")
        var contrastBtn = holder.getView<ImageView>(R.id.iv_contrast)
        holder.setText(R.id.item_works_name, item.name)
        holder.setText(R.id.item_works_source, "来源：${item.author}")
        var vipIcon = holder.getView<ImageView>(R.id.icon_vip)
        var imageView = holder.getView<ShapeableImageView>(R.id.item_works_cover)
        if (item.thumbWidth > 0 && item.thumbHeight > 0) {
            var biliary = item.thumbWidth.toFloat() / item.thumbHeight.toFloat()
            LogUtils.dTag("WorksAdapter", "name:${item.name},biliary:$biliary")
        }
//        imageView.setImageViewRatio(item.thumbWidth, item.thumbHeight)
        GlideHelper.load(imageView, item.thumb, R.color.color_DDDDDD)
        if (holder.layoutPosition + 4 == data.size && CacheUtil.getUser()?.memberType == 1) {
            listener.loadMore()
//            EventBus.getDefault().post(LoadMoreEvent(true))
        }
        if (item.pay == 1) {
            vipIcon.visible()
        } else {
            vipIcon.gone()
        }
        contrastBtn.onClick {
            mClickContrastListener?.invoke(holder.layoutPosition)
        }
    }
}