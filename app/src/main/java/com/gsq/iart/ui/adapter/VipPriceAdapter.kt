package com.gsq.iart.ui.adapter

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gsq.iart.R
import com.gsq.iart.data.bean.PayConfigBean

class VipPriceAdapter constructor(private var type: Int):
    BaseQuickAdapter<PayConfigBean, BaseViewHolder>(R.layout.item_price_layout) {

    var selectBean: PayConfigBean? = null

    fun selectItem(bean: PayConfigBean) {
        selectBean = bean
        notifyDataSetChanged()
    }

    override fun convert(holder: BaseViewHolder, item: PayConfigBean) {
        var itemRoot = holder.getView<ConstraintLayout>(R.id.item_root)
        var itemMonth = holder.getView<TextView>(R.id.item_month)
        var itemPrice = holder.getView<TextView>(R.id.item_price)
        itemMonth.text = "${item.termDesc}"
        itemPrice.text = "￥${item.payAmount}"
        selectBean?.let {
            if (it == item) {
                itemRoot.isSelected = true
                itemPrice.isSelected = true
            } else {
                itemRoot.isSelected = false
                itemPrice.isSelected = false
            }
        } ?: let {
            itemRoot.isSelected = false
            itemPrice.isSelected = false
        }

        if(type == 99){
            itemRoot.setBackgroundResource(R.drawable.bg_price_selector_1)
        }else if(type == 1){
            itemRoot.setBackgroundResource(R.drawable.bg_price_selector)
        }
    }
}