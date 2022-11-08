package com.gsq.iart.ui.adapter

import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gsq.iart.R
import com.gsq.iart.data.bean.ConditionClassifyBean

class AllConditionAdapter constructor(private var isFirst: Boolean) :
    BaseQuickAdapter<ConditionClassifyBean, BaseViewHolder>(R.layout.item_condition_all_layout) {

    override fun convert(holder: BaseViewHolder, item: ConditionClassifyBean) {
        var mName = holder.getView<TextView>(R.id.tv_name)
        var mRecyclerView = holder.getView<RecyclerView>(R.id.recycler_view)
        if (isFirst) {
            mName.setTextColor(mName.context.resources.getColor(R.color.color_141414))
        } else {
            mName.setTextColor(mName.context.resources.getColor(R.color.color_888888))
        }
        mName.text = item.name
        if (item.subs != null) {
            if (item.subs[0].subs != null) {
                mRecyclerView.layoutManager = LinearLayoutManager(mRecyclerView.context)
                var mChildAdapter = AllConditionAdapter(false)
                mRecyclerView.adapter = mChildAdapter
                mChildAdapter.data = item.subs as MutableList<ConditionClassifyBean>
            } else {
                mRecyclerView.layoutManager = GridLayoutManager(mRecyclerView.context, 2)
                var mChildAdapter = ConditionChildAdapter()
                mRecyclerView.adapter = mChildAdapter
                mChildAdapter.data = item.subs as MutableList<ConditionClassifyBean>
                mChildAdapter.setOnItemClickListener { adapter, view, position ->
                    mChildAdapter.data.forEach {
                        it.isSelected = false
                    }
                    //回调选中
                    mChildAdapter.data[position].isSelected = true
                    mChildAdapter?.notifyDataSetChanged()
                }
            }
        }
    }
}