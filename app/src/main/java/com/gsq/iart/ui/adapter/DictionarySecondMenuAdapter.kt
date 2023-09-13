package com.gsq.iart.ui.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gsq.iart.R
import com.gsq.iart.data.bean.DictionaryMenuBean

class DictionarySecondMenuAdapter : BaseQuickAdapter<DictionaryMenuBean, BaseViewHolder>(R.layout.item_dictionary_second_layout) {

    override fun convert(holder: BaseViewHolder, item: DictionaryMenuBean) {
        var mMenuName = holder.getView<TextView>(R.id.menu_name)
        mMenuName.text = item.name
    }

}