package com.gsq.iart.ui.adapter

import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gsq.iart.R
import com.gsq.iart.data.bean.DictionaryMenuBean
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.visible

class DictionarySecondMenuAdapter : BaseQuickAdapter<DictionaryMenuBean, BaseViewHolder>(R.layout.item_dictionary_second_layout) {

    var isExpand = false

    var min_count = 5

    init {
        addChildClickViewIds(R.id.frame_down_layout)
    }

    fun setData(list: MutableList<DictionaryMenuBean>){
        if(list.size> min_count+1 && !isExpand){
            this.data = list.subList(0,min_count+1)
        }else {
            this.data = list
        }
        notifyDataSetChanged()
    }

    fun setExpendStatus(isExpand: Boolean){
        this.isExpand = isExpand

//        notifyDataSetChanged()
    }

    override fun convert(holder: BaseViewHolder, item: DictionaryMenuBean) {
        var frameDownLayout = holder.getView<LinearLayout>(R.id.frame_down_layout)
        var mMenuName = holder.getView<TextView>(R.id.menu_name)
        mMenuName.text = item.name

        if(isExpand){
            //展开
            frameDownLayout.gone()
            mMenuName.visible()
        }else{
            //收起
            if(holder.layoutPosition == min_count){
                frameDownLayout.visible()
                mMenuName.gone()
            }else{
                frameDownLayout.gone()
                mMenuName.visible()
            }
        }
    }

}