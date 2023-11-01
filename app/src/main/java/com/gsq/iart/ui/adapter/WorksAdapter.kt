package com.gsq.iart.ui.adapter

import android.widget.ImageView
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.gsq.iart.BuildConfig
import com.gsq.iart.R
import com.gsq.iart.app.ext.setAdapterAnimation
import com.gsq.iart.app.ext.setImageViewRatio
import com.gsq.iart.app.image.GlideHelper
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.SettingUtil
import com.gsq.iart.data.bean.WorksBean
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.visible

class WorksAdapter constructor(var listener: CallBackListener) :
    BaseQuickAdapter<WorksBean, BaseViewHolder>(R.layout.item_works_layout) {

    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }

    interface CallBackListener {
        fun loadMore()
    }

    override fun convert(holder: BaseViewHolder, item: WorksBean) {
        LogUtils.dTag("WorksAdapter", "layoutPosition:${holder.layoutPosition}")
        holder.setText(R.id.item_works_name, item.name)
        holder.setText(R.id.item_works_desc, "${item.author}  ${item.age}")
        var vipIcon = holder.getView<ImageView>(R.id.icon_vip)
        var imageView = holder.getView<ShapeableImageView>(R.id.item_works_cover)
        if (item.thumbWidth > 0 && item.thumbHeight > 0) {
            var biliary = item.thumbWidth.toFloat() / item.thumbHeight.toFloat()
            LogUtils.dTag("WorksAdapter", "name:${item.name},biliary:$biliary")
        }
        imageView.setImageViewRatio(item.thumbWidth, item.thumbHeight)
        GlideHelper.load(imageView, item.thumb, R.color.color_DDDDDD)
        if (holder.layoutPosition + 4 == data.size) {
            if(CacheUtil.getUser()?.memberType != 1 && BuildConfig.DEBUG){
                return
            }
            listener.loadMore()
//            EventBus.getDefault().post(LoadMoreEvent(true))
        }
        if (item.pay == 1) {
            vipIcon.visible()
        } else {
            vipIcon.gone()
        }
    }
}