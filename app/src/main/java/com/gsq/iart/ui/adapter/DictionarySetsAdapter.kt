package com.gsq.iart.ui.adapter

import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gsq.iart.R
import com.gsq.iart.app.App
import com.gsq.iart.app.image.GlideHelper
import com.gsq.iart.data.bean.DictionarySetsBean
import kotlinx.android.synthetic.main.fragment_member.agreement_privacy

class DictionarySetsAdapter :
    BaseQuickAdapter<DictionarySetsBean, BaseViewHolder>(R.layout.item_dictionary_sets_layout) {

    override fun convert(holder: BaseViewHolder, item: DictionarySetsBean) {
        var imageView = holder.getView<ImageView>(R.id.iv_image)
        holder.getView<TextView>(R.id.tv_name).text = item.name
        var countView = holder.getView<TextView>(R.id.tv_count)

        SpanUtils.with(countView)
            .append("收录")
            .append("${item.num}")
            .setForegroundColor(ColorUtils.getColor(R.color.color_141414))
            .append("幅切图")
            .create()
        GlideHelper.load(imageView, item.img)
    }
}