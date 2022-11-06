package com.gsq.iart.ui.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gsq.iart.R
import com.gsq.iart.data.bean.ConditionClassifyBean

class ConditionChildAdapter :
    BaseQuickAdapter<ConditionClassifyBean, BaseViewHolder>(R.layout.item_condition_child_layout) {

    override fun convert(holder: BaseViewHolder, item: ConditionClassifyBean) {
        var mName = holder.getView<TextView>(R.id.tv_name)
        mName.text = item.name
    }
}