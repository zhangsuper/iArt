package com.gsq.iart.ui.dialog

import android.animation.Animator
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gsq.iart.R
import com.xu.xpopupwindow.XPopupWindow

class ConditionPopupWindow: XPopupWindow {

    private var recyclerView: RecyclerView? = null
    private var resetBtn: TextView? = null
    private var requireBtn: LinearLayout? = null

    constructor(ctx: Context, w: Int, h: Int) : super(ctx, w, h)

    override fun getLayoutId(): Int {
        return R.layout.popup_condition
    }

    override fun getLayoutParentNodeId(): Int {
        return R.id.popup_window_parent
    }

    override fun initViews() {
        recyclerView = findViewById(R.id.recycler_view)
        resetBtn = findViewById(R.id.reset_btn)
        requireBtn = findViewById(R.id.require_btn)
        resetBtn?.setOnClickListener {
            dismiss()
        }
        requireBtn?.setOnClickListener {
            dismiss()
        }
    }

    override fun initData() {
    }

    override fun startAnim(view: View): Animator? {
        return null
    }

    override fun exitAnim(view: View): Animator? {
        return null
    }

    override fun animStyle(): Int {
        return -1
    }

}