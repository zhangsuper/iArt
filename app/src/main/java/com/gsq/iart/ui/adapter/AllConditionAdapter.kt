package com.gsq.iart.ui.adapter

import android.graphics.Typeface
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gsq.iart.R
import com.gsq.iart.data.bean.ConditionClassifyBean

class AllConditionAdapter constructor(private var isFirst: Boolean) :
    BaseQuickAdapter<ConditionClassifyBean, BaseViewHolder>(R.layout.item_condition_all_layout) {

    override fun convert(holder: BaseViewHolder, item: ConditionClassifyBean) {
        var mName = holder.getView<TextView>(R.id.tv_name)
        var mRecyclerView = holder.getView<RecyclerView>(R.id.recycler_view)
        var nameParams = mName.layoutParams as ConstraintLayout.LayoutParams
        var recyclerViewParams = mRecyclerView.layoutParams as ConstraintLayout.LayoutParams
        if (isFirst) {
            mName.setTextColor(mName.context.resources.getColor(R.color.color_141414))
            mName.setTypeface(null, Typeface.BOLD)
            nameParams.leftMargin = SizeUtils.dp2px(24f)
            recyclerViewParams.leftMargin = SizeUtils.dp2px(24f)
        } else {
            mName.setTextColor(mName.context.resources.getColor(R.color.color_888888))
            mName.setTypeface(null, Typeface.NORMAL)
            nameParams.leftMargin = SizeUtils.dp2px(0f)
            recyclerViewParams.leftMargin = SizeUtils.dp2px(0f)
        }
        mName.text = item.name
        if (item.subs != null) {
            if (item.subs[0].subs != null) {
                mRecyclerView.layoutManager = LinearLayoutManager(mRecyclerView.context)
                var mChildAdapter = AllConditionAdapter(false)
                mRecyclerView.adapter = mChildAdapter
                mChildAdapter.data = item.subs as MutableList<ConditionClassifyBean>
                mChildAdapter.onBackListener(object : OnBackListener {
                    override fun onClick() {
                        mChildAdapter.data.forEach {
                            if (it.subs != null) {
                                it.subs.forEach {
                                    it.isSelected = false
                                }
                            }
                        }
                        mChildAdapter?.notifyDataSetChanged()
                    }
                })

            } else {
                mRecyclerView.layoutManager = GridLayoutManager(mRecyclerView.context, 2)
                var mChildAdapter = ConditionChildAdapter()
                mRecyclerView.adapter = mChildAdapter
                mChildAdapter.data = item.subs as MutableList<ConditionClassifyBean>
                mChildAdapter.setOnItemClickListener { adapter, view, position ->
                    onBackListener?.onClick()
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

    private var onBackListener: OnBackListener? = null

    interface OnBackListener {
        fun onClick()
    }

    fun onBackListener(listener: OnBackListener): AllConditionAdapter {
        this.onBackListener = listener
        return this
    }
}