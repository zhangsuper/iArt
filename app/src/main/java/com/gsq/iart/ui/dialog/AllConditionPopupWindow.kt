package com.gsq.iart.ui.dialog

import android.animation.Animator
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gsq.iart.R
import com.gsq.iart.data.bean.ConditionClassifyBean
import com.gsq.iart.ui.adapter.AllConditionAdapter
import com.xu.xpopupwindow.XPopupWindow

/**
 * 全部筛选条件弹窗
 */
class AllConditionPopupWindow : XPopupWindow {

    private var recyclerView: RecyclerView? = null
    private var resetBtn: TextView? = null
    private var requireBtn: LinearLayout? = null

    private var mAdapter: AllConditionAdapter? = null

    private lateinit var dataList: MutableList<ConditionClassifyBean>
    private var currentSelectGrade1Item: ConditionClassifyBean? = null

    constructor(ctx: Context, w: Int, h: Int) : super(ctx, w, h)

    private var onBackListener: OnBackListener? = null

    interface OnBackListener {
        fun onItemClick(grade1Item: ConditionClassifyBean, grade2Item: ConditionClassifyBean?)
        fun onDismiss()
        fun onReset()
    }

//    private var onBackListener: ((grade1Item: ConditionClassifyBean,grade2Item: ConditionClassifyBean?) -> Unit)? = null

    fun onBackListener(listener: OnBackListener): AllConditionPopupWindow {
        this.onBackListener = listener
        return this
    }

    override fun getLayoutId(): Int {
        return R.layout.popup_all_condition
    }

    override fun getLayoutParentNodeId(): Int {
        return R.id.popup_window_parent
    }

    override fun initViews() {
        recyclerView = findViewById(R.id.recycler_view)
        resetBtn = findViewById(R.id.reset_btn)
        requireBtn = findViewById(R.id.require_btn)
        resetBtn?.setOnClickListener {
//            leftData.forEach {
//                it.isSelected = false
//            }
//            rightAdapter?.data?.forEach {
//                it.isSelected = false
//            }
//            onBackListener?.onReset()
            dismiss()
        }
        requireBtn?.setOnClickListener {
//            currentSelectGrade1Item?.let { item1 ->
//                currentSelectGrade2Item?.let { itme2 ->
//                    onBackListener?.onItemClick(item1, itme2)
//                    dismiss()
//                } ?: let {
//                    if (item1.subs != null && item1.subs.isNotEmpty()) {
//                        ToastUtils.showLong("请选择类型")
//                    } else {
//                        onBackListener?.onItemClick(item1, null)
//                        dismiss()
//                    }
//                }
//            } ?: let {
//                ToastUtils.showLong("请选择类型")
//            }
        }
        mAdapter = AllConditionAdapter(true)
        dataList = mutableListOf()
        recyclerView?.adapter = mAdapter

        mAdapter?.setOnItemClickListener { adapter, view, position ->
//            if (dataList[position].subs != null) {
//                rightAdapter?.data = (leftData[position].subs as MutableList<ConditionClassifyBean>)
//                rightAdapter?.notifyDataSetChanged()
//                rightRecyclerView?.visible()
//            }
//            leftData.forEach {
//                it.isSelected = false
//            }
//            //回调选中
//            currentSelectGrade1Item?.isSelected = false
//            currentSelectGrade1Item = leftData[position]
//            currentSelectGrade1Item?.isSelected = true
//            leftAdapter?.notifyDataSetChanged()
        }
    }

    fun setData(list: List<ConditionClassifyBean>, selectedItem: List<ConditionClassifyBean>?) {
        dataList = (list as MutableList<ConditionClassifyBean>)
//        leftAdapter?.notifyDataSetChanged()
        mAdapter?.data = dataList

//        selectedItem?.let {
//            currentSelectGrade1Item = it[0]
//            if (it.size > 1) {
//                currentSelectGrade2Item = it[1]
//            }
//        }
//        if (currentSelectGrade1Item != null && currentSelectGrade2Item != null) {
//            var position = -1
//            for (index in leftData.indices) {
//                if (leftData[index].id == currentSelectGrade1Item!!.id) {
//                    position = index
//                    break
//                }
//            }
//            if (leftData[position].subs != null) {
//                rightAdapter?.data = (leftData[position].subs as MutableList<ConditionClassifyBean>)
//                rightAdapter?.notifyDataSetChanged()
//                rightRecyclerView?.visible()
//            }
//        }
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

    override fun dismiss() {
        super.dismiss()
        onBackListener?.onDismiss()
    }

}