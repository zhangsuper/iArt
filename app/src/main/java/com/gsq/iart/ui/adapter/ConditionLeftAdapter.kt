package com.gsq.iart.ui.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gsq.iart.R
import com.gsq.iart.data.bean.ConditionBean

class ConditionLeftAdapter: BaseQuickAdapter<ConditionBean, BaseViewHolder>(R.layout.item_condition_left_layout) {

    override fun convert(holder: BaseViewHolder, item: ConditionBean) {
        holder.getView<TextView>(R.id.tv_content).text = item.content
    }
}