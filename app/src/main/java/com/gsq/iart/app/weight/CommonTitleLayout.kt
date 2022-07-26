package com.gsq.iart.app.weight

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.gsq.iart.R

class CommonTitleLayout: RelativeLayout {

    private var iv_back: ImageView
    private var tv_title: TextView

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
        typedArray.recycle()

        iv_back = view.findViewById(R.id.iv_back)
        tv_title = view.findViewById(R.id.tv_title)
        tv_title.text = titleString
    }


    fun setBackListener(listener: () -> Unit) {
        iv_back.setOnClickListener {
            listener.invoke()
        }
    }

    fun setTitle(title: String) {
        tv_title.text = title
    }


}