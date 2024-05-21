package com.gsq.iart.app.weight

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.gsq.iart.R

class OpenMemberFooterLayout : LinearLayout {

    private var open_vip_btn: TextView

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        val view = View.inflate(context, R.layout.layout_open_member_footer, this)
        open_vip_btn = view.findViewById(R.id.open_vip_btn)
    }

}