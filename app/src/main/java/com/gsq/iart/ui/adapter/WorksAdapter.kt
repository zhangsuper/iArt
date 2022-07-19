package com.gsq.iart.ui.adapter

import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.gsq.iart.R
import com.gsq.iart.app.ext.setAdapterAnimation
import com.gsq.iart.app.image.GlideHelper
import com.gsq.iart.app.util.SettingUtil
import com.gsq.iart.data.bean.WorksBean

class WorksAdapter: BaseQuickAdapter<WorksBean, BaseViewHolder>(R.layout.item_works_layout) {

    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: WorksBean) {
        holder.setText(R.id.item_works_name, item.title)
        holder.setText(R.id.item_works_desc, item.author)
        var imageView = holder.getView<ShapeableImageView>(R.id.item_works_cover)
        GlideHelper.load(imageView, item.envelopePic)
        if (holder.layoutPosition == 1){
            imageView.layoutParams.height = SizeUtils.dp2px(318f)
        }else{
            imageView.layoutParams.height = SizeUtils.dp2px(208f)
        }
    }
}