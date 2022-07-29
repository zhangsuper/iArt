package com.gsq.iart.ui.adapter

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gsq.iart.R
import com.gsq.iart.data.bean.VipPriceBean

class VipPriceAdapter: BaseQuickAdapter<VipPriceBean, BaseViewHolder>(R.layout.item_price_layout) {

    private var selectBean: VipPriceBean? = null

    fun selectItem(bean: VipPriceBean){
        selectBean = bean
        notifyDataSetChanged()
    }

    override fun convert(holder: BaseViewHolder, item: VipPriceBean) {
        var itemRoot = holder.getView<ConstraintLayout>(R.id.item_root)
        var itemMonth = holder.getView<TextView>(R.id.item_month)
        var itemPrice = holder.getView<TextView>(R.id.item_price)
        itemMonth.text = "${item.month}个月"
        itemPrice.text = "￥${item.price}"
        selectBean?.let {
           if(it == item){
               itemRoot.isSelected = true
               itemPrice.isSelected = true
           }else{
               itemRoot.isSelected = false
               itemPrice.isSelected = false
           }
        }?: let {
            itemRoot.isSelected = false
            itemPrice.isSelected = false
        }
    }
}