package com.gsq.iart.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
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
        GlideHelper.load(holder.getView(R.id.item_works_cover), item.envelopePic)
    }
}