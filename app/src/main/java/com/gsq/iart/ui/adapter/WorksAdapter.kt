package com.gsq.iart.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gsq.iart.R
import com.gsq.iart.data.bean.WorksBean

class WorksAdapter: BaseQuickAdapter<WorksBean, BaseViewHolder>(R.layout.item_works_layout) {

    override fun convert(holder: BaseViewHolder, item: WorksBean) {
    }
}