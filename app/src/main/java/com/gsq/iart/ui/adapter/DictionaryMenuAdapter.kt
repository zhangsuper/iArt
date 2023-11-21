package com.gsq.iart.ui.adapter

import android.graphics.Typeface
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gsq.iart.R
import com.gsq.iart.app.image.GlideHelper
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.weight.recyclerview.GridItemDecoration
import com.gsq.iart.data.bean.DictionaryMenuBean
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.onClick
import com.gsq.mvvm.ext.view.visible
import kotlinx.android.synthetic.main.fragment_mine.iv_avatar

class DictionaryMenuAdapter : BaseQuickAdapter<DictionaryMenuBean, BaseViewHolder>(R.layout.item_dictionary_layout) {

    private var mClickListener: ((bean: DictionaryMenuBean, position: Int) -> Unit)? = null
    private var onExtendClickListener: (() -> Unit)? = null

    fun setClickBackListener(listener: ((bean: DictionaryMenuBean, position: Int) -> Unit)?){
        mClickListener = listener
    }

    fun setExtendClickListener(listener: (() -> Unit)?) {
        onExtendClickListener = listener
    }

    override fun convert(holder: BaseViewHolder, item: DictionaryMenuBean) {
        var leftView = holder.getView<LinearLayoutCompat>(R.id.left_view)
        var mMenuLogo = holder.getView<ImageView>(R.id.menu_logo)
        var mMenuName = holder.getView<TextView>(R.id.menu_name)
        var mRecyclerView = holder.getView<RecyclerView>(R.id.recycler_view)
        var frameUpLayout = holder.getView<LinearLayout>(R.id.frame_up_layout)
        mRecyclerView.layoutManager  = StaggeredGridLayoutManager(
            3, StaggeredGridLayoutManager.VERTICAL
        )
        mRecyclerView.addItemDecoration(
            GridItemDecoration(
                3,
                SizeUtils.dp2px(10f),
                includeEdge = true, hasTopBottomSpace = true
            )
        )
        var mSecondMenuAdapter = DictionarySecondMenuAdapter()
//        mSecondMenuAdapter.min_count = 2

        GlideHelper.load(mMenuLogo, item.icon)
        mRecyclerView.adapter = mSecondMenuAdapter
        mMenuName.text = item.name
        if(item.subs!= null){
            mSecondMenuAdapter.setData(item.subs)
        }
        mSecondMenuAdapter.setOnItemClickListener { adapter, view, position ->
            mClickListener?.invoke(item, position+1)
        }
        mSecondMenuAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.frame_down_layout) {
                //展开 需要超级会员
                if(CacheUtil.getUser()?.memberType != 1){
                    ToastUtils.showLong("需要超级会员才能展开")
                    onExtendClickListener?.invoke()
                }else {
                    mSecondMenuAdapter.setExpendStatus(true)
                    mSecondMenuAdapter.setData(item.subs)
                    frameUpLayout.visible()
                }
            }
        }
        frameUpLayout.onClick {
            //收起
            frameUpLayout.gone()
            mSecondMenuAdapter.setExpendStatus(false)
            mSecondMenuAdapter.setData(item.subs)
        }
        leftView.onClick {
            mClickListener?.invoke(item,0)
        }
    }

}