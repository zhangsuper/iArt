package com.gsq.iart.ui.adapter

import android.widget.ImageView
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.gsq.iart.R
import com.gsq.iart.app.ext.setAdapterAnimation
import com.gsq.iart.app.ext.setImageViewRatio
import com.gsq.iart.app.image.GlideHelper
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.SettingUtil
import com.gsq.iart.data.Constant.COMPLEX_TYPE_COMPARE
import com.gsq.iart.data.bean.DictionaryArgsType
import com.gsq.iart.data.bean.DictionaryMenuBean
import com.gsq.iart.data.bean.DictionaryWorksBean
import com.gsq.iart.data.bean.WorksBean
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.onClick
import com.gsq.mvvm.ext.view.visible

class DictionaryWorksAdapter constructor(var listener: CallBackListener) :
    BaseQuickAdapter<DictionaryWorksBean, BaseViewHolder>(R.layout.item_dictionary_works_layout) {

    var compareList: ArrayList<DictionaryWorksBean>
    private var args: DictionaryArgsType? = null

    init {
        setAdapterAnimation(SettingUtil.getListMode())
        addChildClickViewIds(R.id.iv_contrast)
        compareList = CacheUtil.getCompareList()
    }

    interface CallBackListener {
        fun loadMore()
    }

    fun setArgsType(args: DictionaryArgsType){
        this.args = args
    }

    fun updateCompareList(){
        compareList = CacheUtil.getCompareList()
    }

    override fun convert(holder: BaseViewHolder, item: DictionaryWorksBean) {
        LogUtils.dTag("WorksAdapter", "layoutPosition:${holder.layoutPosition}")
        var contrastBtn = holder.getView<ImageView>(R.id.iv_contrast)
        holder.setText(R.id.item_works_name, item.name)
        holder.setText(R.id.item_works_source, "来源：[${item.mainAge}]${item.mainName}")
        var vipIcon = holder.getView<ImageView>(R.id.icon_vip)
        var imageView = holder.getView<ShapeableImageView>(R.id.item_works_cover)
        if (item.thumbWidth > 0 && item.thumbHeight > 0) {
            var biliary = item.thumbWidth.toFloat() / item.thumbHeight.toFloat()
            LogUtils.dTag("WorksAdapter", "name:${item.name},biliary:$biliary")
        }
//        imageView.setImageViewRatio(item.thumbWidth, item.thumbHeight)
        GlideHelper.load(imageView, item.thumb, R.color.color_DDDDDD)
        if(args?.firstTag == COMPLEX_TYPE_COMPARE){
            contrastBtn.gone()
        }else {
            contrastBtn.visible()
        }
        if (compareList.find { it.workId == item.workId } != null) {
            item.isAddCompare = true
            contrastBtn.setImageResource(R.drawable.compare_remove)
        } else {
            contrastBtn.setImageResource(R.drawable.icon_add)
            item.isAddCompare = false
        }

        if (holder.layoutPosition + 4 == data.size && CacheUtil.getUser()?.memberType == 1) {
            listener.loadMore()
//            EventBus.getDefault().post(LoadMoreEvent(true))
        }
        if (item.pay == 1) {
            vipIcon.visible()
        } else {
            vipIcon.gone()
        }

    }
}