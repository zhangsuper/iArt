package com.gsq.iart.app.weight

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.gsq.iart.R
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.visible

class CommonItemLayout : RelativeLayout {

    private var item_root: ConstraintLayout
    private var item_title: TextView
    private var common_item_operation: TextView
    private var common_item_sub_desc: TextView
    private var item_divider: View
    private var item_right_icon: ImageView

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        val view = View.inflate(context, R.layout.common_item_layout, this)
        item_root = view.findViewById(R.id.mine_item_rootView)
        item_title = view.findViewById(R.id.common_item_title)
        item_divider = view.findViewById(R.id.common_item_divider)
        common_item_operation = view.findViewById(R.id.common_item_operation)
        common_item_sub_desc = view.findViewById(R.id.common_item_sub_desc)
        item_right_icon = view.findViewById(R.id.common_item_allow)


        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonItemLayout)
        val itemTitleString = typedArray.getString(R.styleable.CommonItemLayout_item_title)
        val itemRightMsgString = typedArray.getString(R.styleable.CommonItemLayout_item_right_msg)
        val itemRightSubMsgString =
            typedArray.getString(R.styleable.CommonItemLayout_item_right_sub_msg)
        val itemRightIconVisible =
            typedArray.getBoolean(R.styleable.CommonItemLayout_item_right_icon_visible, true)
        val itemDividerVisible =
            typedArray.getBoolean(R.styleable.CommonItemLayout_item_divider_visible, true)

        typedArray.recycle()


        //设置左侧标题 默认展示
        itemTitleString?.let {
            item_title.text = it
        }

        //右侧图标
        if (itemRightIconVisible) {
            item_right_icon.visible()
        } else {
            item_right_icon.gone()
        }

        //底部分割线
        if (itemDividerVisible) {
            item_divider.visible()
        } else {
            item_divider.gone()
        }

        itemRightMsgString?.let {
            common_item_operation.text = it
        }

        itemRightSubMsgString?.let {
            common_item_sub_desc.text = it
        }
    }

    fun getRightSubDescTv(): TextView {
        return common_item_sub_desc
    }


    fun setRightMsgListener(listener: () -> Unit) {
        common_item_operation.setOnClickListener {
            listener.invoke()
        }
    }

    fun setItemDividerVisible(isVisible: Boolean) {
        if (isVisible) {
            item_divider.visible()
        } else {
            item_divider.gone()
        }

    }
}