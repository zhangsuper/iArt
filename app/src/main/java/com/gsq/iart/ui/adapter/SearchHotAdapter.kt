package com.gsq.iart.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gsq.iart.R
import com.gsq.iart.app.ext.setAdapterAnimation
import com.gsq.iart.app.util.SettingUtil
import com.gsq.iart.data.bean.SearchResponse

class SearchHotAdapter(data: MutableList<SearchResponse>) :
    BaseQuickAdapter<SearchResponse, BaseViewHolder>(R.layout.item_search_hot, data) {

    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: SearchResponse) {
        holder.setText(R.id.flow_tag, item.name)
    }

}