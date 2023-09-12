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
import com.gsq.iart.data.bean.DictionaryMenuBean

class DictionaryMenuAdapter : BaseQuickAdapter<DictionaryMenuBean, BaseViewHolder>(R.layout.item_condition_all_layout) {

    override fun convert(holder: BaseViewHolder, item: DictionaryMenuBean) {
        var mName = holder.getView<TextView>(R.id.tv_name)
        var mRecyclerView = holder.getView<RecyclerView>(R.id.recycler_view)
        var nameParams = mName.layoutParams as ConstraintLayout.LayoutParams
        var recyclerViewParams = mRecyclerView.layoutParams as ConstraintLayout.LayoutParams

    }

    private var onBackListener: OnBackListener? = null

    interface OnBackListener {
        fun onClick()
    }

    fun onBackListener(listener: OnBackListener): DictionaryMenuAdapter {
        this.onBackListener = listener
        return this
    }
}