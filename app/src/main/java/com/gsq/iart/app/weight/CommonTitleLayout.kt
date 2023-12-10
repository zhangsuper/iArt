package com.gsq.iart.app.weight

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.gsq.iart.R
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.visible

class CommonTitleLayout : RelativeLayout {

    private var iv_back: ImageView
    private var tv_title: TextView
    private var tv_right: TextView
    private var iv_center: ImageView

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        val view = View.inflate(context, R.layout.common_title_layout, this)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleLayout)
        val titleString = typedArray.getString(R.styleable.CommonTitleLayout_title_string)
        val rightString = typedArray.getString(R.styleable.CommonTitleLayout_right_string)
        typedArray.recycle()

        iv_back = view.findViewById(R.id.iv_back)
        tv_title = view.findViewById(R.id.tv_title)
        tv_right = view.findViewById(R.id.tv_right)
        iv_center = view.findViewById(R.id.iv_center)
        tv_title.text = titleString
        tv_right.text = rightString
    }


    fun setBackListener(listener: () -> Unit) {
        iv_back.setOnClickListener {
            listener.invoke()
        }
    }

    fun setRightClickListener(listener: () -> Unit) {
        tv_right.setOnClickListener {
            listener.invoke()
        }
    }

    fun setCenterClickListener(listener: () -> Unit) {
        iv_center.setOnClickListener {
            listener.invoke()
        }
    }

    fun setTitle(title: String) {
        tv_title.text = title
    }

    fun setRightText(text: String) {
        tv_right.text = text
        if (text.isNullOrEmpty()) {
            tv_right.gone()
        } else {
            tv_right.visible()
        }
    }

    fun setCenterImage(img: Int) {
        iv_center.visible()
        iv_center.setImageResource(img)
    }


}