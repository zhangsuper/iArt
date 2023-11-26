package com.gsq.iart.ui.adapter

import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gsq.iart.R
import com.gsq.iart.app.image.GlideHelper
import com.gsq.iart.data.bean.DictionarySetsBean

class DictionarySetsAdapter :
    BaseQuickAdapter<DictionarySetsBean, BaseViewHolder>(R.layout.item_dictionary_sets_layout) {

    override fun convert(holder: BaseViewHolder, item: DictionarySetsBean) {
        var imageView = holder.getView<ImageView>(R.id.iv_image)
        holder.getView<TextView>(R.id.tv_name).text = item.name
        holder.getView<TextView>(R.id.tv_count).text = "收录${item.num}幅切图"
        GlideHelper.load(imageView, item.img)
    }
}