package com.gsq.iart.ui.dialog

import android.animation.Animator
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gsq.iart.R
import com.gsq.iart.data.bean.ConditionBean
import com.gsq.iart.ui.adapter.ConditionLeftAdapter
import com.gsq.iart.ui.adapter.ConditionRightAdapter
import com.gsq.mvvm.ext.view.visible
import com.xu.xpopupwindow.XPopupWindow

class ConditionPopupWindow: XPopupWindow {

    private var leftRecyclerView: RecyclerView? = null
    private var rightRecyclerView: RecyclerView? = null
    private var resetBtn: TextView? = null
    private var requireBtn: LinearLayout? = null

    private var leftAdapter: ConditionLeftAdapter? = null
    private var rightAdapter: ConditionRightAdapter? = null

    constructor(ctx: Context, w: Int, h: Int) : super(ctx, w, h)

    override fun getLayoutId(): Int {
        return R.layout.popup_condition
    }

    override fun getLayoutParentNodeId(): Int {
        return R.id.popup_window_parent
    }

    override fun initViews() {
        leftRecyclerView = findViewById(R.id.left_recycler_view)
        rightRecyclerView = findViewById(R.id.right_recycler_view)
        resetBtn = findViewById(R.id.reset_btn)
        requireBtn = findViewById(R.id.require_btn)
        resetBtn?.setOnClickListener {
            dismiss()
        }
        requireBtn?.setOnClickListener {
            dismiss()
        }
        leftAdapter = ConditionLeftAdapter()
        rightAdapter = ConditionRightAdapter()
        leftRecyclerView?.adapter = leftAdapter
        rightRecyclerView?.adapter = rightAdapter
    }

    override fun initData() {
        var leftData = mutableListOf<ConditionBean>()
        var bean1 = ConditionBean("人物",1)
        var bean2 = ConditionBean("山水",2)
        var bean3 = ConditionBean("花鸟",3)
        leftData.add(bean1)
        leftData.add(bean2)
        leftData.add(bean3)
        leftAdapter?.data = leftData

        var rightData = mutableListOf<ConditionBean>()
        var rightbean1 = ConditionBean("历史人物",1)
        var rightbean2 = ConditionBean("道释画",2)
        var rightbean3 = ConditionBean("高士",3)
        var rightbean4 = ConditionBean("肖像",4)
        rightData.add(rightbean1)
        rightData.add(rightbean2)
        rightData.add(rightbean3)
        rightData.add(rightbean4)
        rightAdapter?.data = rightData
        rightRecyclerView?.visible()
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