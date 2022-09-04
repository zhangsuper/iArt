package com.gsq.iart.ui.adapter

import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gsq.iart.R
import com.gsq.iart.data.bean.ConditionClassifyBean
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.visible

class ConditionLeftAdapter: BaseQuickAdapter<ConditionClassifyBean, BaseViewHolder>(R.layout.item_condition_left_layout) {

    override fun convert(holder: BaseViewHolder, item: ConditionClassifyBean) {
        var mName = holder.getView<TextView>(R.id.tv_content)
        var mSelectLine = holder.getView<View>(R.id.selected_line)
        var mSelectIcon = holder.getView<View>(R.id.iv_selected)
        mName.text = item.name
        if(item.isSelected){
            mName.isSelected = true
            mSelectLine.visible()
            mSelectIcon.visible()
        }else{
            mName.isSelected = false
            mSelectLine.gone()
            mSelectIcon.gone()
        }
    }
}