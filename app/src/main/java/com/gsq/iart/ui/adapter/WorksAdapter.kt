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
import com.gsq.iart.app.util.SettingUtil
import com.gsq.iart.data.bean.WorksBean
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.visible

class WorksAdapter : BaseQuickAdapter<WorksBean, BaseViewHolder>(R.layout.item_works_layout) {

    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: WorksBean) {
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
//        if (holder.layoutPosition == 1) {
//            imageView.layoutParams.height = SizeUtils.dp2px(318f)
//        } else {
//            imageView.layoutParams.height = SizeUtils.dp2px(208f)
//        }
        if (item.pay == 1) {
            vipIcon.visible()
        } else {
            vipIcon.gone()
        }
    }
}