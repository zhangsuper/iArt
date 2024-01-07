package com.gsq.iart.ui.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gsq.iart.R
import com.gsq.iart.data.bean.DictionaryMenuBean

class DictionaryLevelAdapter(data: MutableList<DictionaryMenuBean>) :
    BaseQuickAdapter<DictionaryMenuBean, BaseViewHolder>(R.layout.item_dictionary_tag, data) {

    init {
//        setAdapterAnimation(SettingUtil.getListMode())
    }
    var selectedPosition: Int = 0

    fun setClickSelectedPosition(position: Int){
        selectedPosition = position
        notifyDataSetChanged()
    }

    override fun convert(holder: BaseViewHolder, item: DictionaryMenuBean) {
        var content = holder.getView<TextView>(R.id.flow_tag)
        content.text = item.name
        selectedPosition?.let {
            content.isSelected = it == holder.layoutPosition
        }?: let {
            content.isSelected= false
        }
    }

}