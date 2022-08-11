package com.gsq.iart.ui.dialog

import android.animation.Animator
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.R
import com.gsq.iart.data.bean.ConditionBean
import com.gsq.iart.data.bean.ConditionClassifyBean
import com.gsq.iart.ui.adapter.ConditionLeftAdapter
import com.gsq.iart.ui.adapter.ConditionRightAdapter
import com.gsq.iart.ui.fragment.search.SearchInitFragment
import com.gsq.mvvm.ext.view.visible
import com.xu.xpopupwindow.XPopupWindow

class ConditionPopupWindow: XPopupWindow {

    private var leftRecyclerView: RecyclerView? = null
    private var rightRecyclerView: RecyclerView? = null
    private var resetBtn: TextView? = null
    private var requireBtn: LinearLayout? = null

    private var leftAdapter: ConditionLeftAdapter? = null
    private var rightAdapter: ConditionRightAdapter? = null

    private lateinit var leftData: MutableList<ConditionClassifyBean>
    private var currentSelectGrade1Item: ConditionClassifyBean? = null
    private var currentSelectGrade2Item: ConditionClassifyBean? = null

    constructor(ctx: Context, w: Int, h: Int) : super(ctx, w, h)

    private var onBackListener: OnBackListener? = null
    interface OnBackListener{
        fun onItemClick(grade1Item: ConditionClassifyBean,grade2Item: ConditionClassifyBean?)
        fun onDismiss()
    }

//    private var onBackListener: ((grade1Item: ConditionClassifyBean,grade2Item: ConditionClassifyBean?) -> Unit)? = null

    fun onBackListener(listener: OnBackListener): ConditionPopupWindow {
        this.onBackListener = listener
        return this
    }

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
            currentSelectGrade1Item?.let { item1 ->
                currentSelectGrade2Item?.let { itme2 ->
                    onBackListener?.onItemClick(item1,itme2)
                    dismiss()
                }?:let {
                    if(item1.subs!= null && item1.subs.isNotEmpty()){
                        ToastUtils.showLong("请选择类型")
                    }else{
                        onBackListener?.onItemClick(item1,null)
                        dismiss()
                    }
                }
            }?:let {
                ToastUtils.showLong("请选择类型")
            }
        }
        leftAdapter = ConditionLeftAdapter()
        leftData = mutableListOf()
        rightAdapter = ConditionRightAdapter()
        leftRecyclerView?.adapter = leftAdapter
        rightRecyclerView?.adapter = rightAdapter


        leftAdapter?.setOnItemClickListener { adapter, view, position ->
            if(leftData[position].subs != null){
                rightAdapter?.data = (leftData[position].subs as MutableList<ConditionClassifyBean>)
                rightAdapter?.notifyDataSetChanged()
                rightRecyclerView?.visible()
            }
            //回调选中
            currentSelectGrade1Item?.isSelected = false
            currentSelectGrade1Item = leftData[position]
            currentSelectGrade1Item?.isSelected = true
            leftAdapter?.notifyDataSetChanged()
        }
        rightAdapter?.setOnItemClickListener { adapter, view, position ->
            //回调选中
            currentSelectGrade2Item?.isSelected = false
            currentSelectGrade2Item = rightAdapter?.data?.get(position)
            currentSelectGrade2Item?.isSelected = true
            rightAdapter?.notifyDataSetChanged()
        }
    }

    fun setData(list: List<ConditionClassifyBean>){
        leftData = (list as MutableList<ConditionClassifyBean>)
//        leftAdapter?.notifyDataSetChanged()
        leftAdapter?.data = leftData
    }

    override fun initData() {
//        var leftData = mutableListOf<ConditionBean>()
//        var bean1 = ConditionBean("人物",1)
//        var bean2 = ConditionBean("山水",2)
//        var bean3 = ConditionBean("花鸟",3)
//        leftData.add(bean1)
//        leftData.add(bean2)
//        leftData.add(bean3)
//        leftAdapter?.data = leftData
//
//        var rightData = mutableListOf<ConditionBean>()
//        var rightbean1 = ConditionBean("历史人物",1)
//        var rightbean2 = ConditionBean("道释画",2)
//        var rightbean3 = ConditionBean("高士",3)
//        var rightbean4 = ConditionBean("肖像",4)
//        rightData.add(rightbean1)
//        rightData.add(rightbean2)
//        rightData.add(rightbean3)
//        rightData.add(rightbean4)
//        rightAdapter?.data = rightData
//        rightRecyclerView?.visible()
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