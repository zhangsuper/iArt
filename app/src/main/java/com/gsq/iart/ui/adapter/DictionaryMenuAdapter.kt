package com.gsq.iart.ui.adapter

import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gsq.iart.R
import com.gsq.iart.app.weight.recyclerview.GridItemDecoration
import com.gsq.iart.data.bean.DictionaryMenuBean

class DictionaryMenuAdapter : BaseQuickAdapter<DictionaryMenuBean, BaseViewHolder>(R.layout.item_dictionary_layout) {

    private var mClickListener: ((bean: DictionaryMenuBean) -> Unit)? = null

    fun setClickBackListener(listener: ((bean: DictionaryMenuBean) -> Unit)?){
        mClickListener = listener
    }

    override fun convert(holder: BaseViewHolder, item: DictionaryMenuBean) {
        var mMenuLogo = holder.getView<ImageView>(R.id.menu_logo)
        var mMenuName = holder.getView<TextView>(R.id.menu_name)
        var mRecyclerView = holder.getView<RecyclerView>(R.id.recycler_view)
        mRecyclerView.layoutManager  = StaggeredGridLayoutManager(
            3, StaggeredGridLayoutManager.VERTICAL
        )
        mRecyclerView.addItemDecoration(
            GridItemDecoration(
                3,
                SizeUtils.dp2px(10f),
                includeEdge = true, hasTopBottomSpace = false
            )
        )
        var mSecondMenuAdapter = DictionarySecondMenuAdapter()
        mRecyclerView.adapter = mSecondMenuAdapter
        mMenuName.text = item.name
        if(item.subs!= null){
            mSecondMenuAdapter.data = item.subs
        }
        mSecondMenuAdapter.setOnItemClickListener { adapter, view, position ->
            mClickListener?.invoke(item)
        }
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