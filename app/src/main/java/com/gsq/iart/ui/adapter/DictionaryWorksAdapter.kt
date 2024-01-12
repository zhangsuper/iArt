package com.gsq.iart.ui.adapter

import android.widget.ImageView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.gsq.iart.R
import com.gsq.iart.app.ext.setAdapterAnimation
import com.gsq.iart.app.ext.setImageViewRatio
import com.gsq.iart.app.image.GlideHelper
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.SettingUtil
import com.gsq.iart.data.Constant
import com.gsq.iart.data.Constant.COMPLEX_TYPE_COMPARE
import com.gsq.iart.data.Constant.COMPLEX_TYPE_NATIVE_COMPARE
import com.gsq.iart.data.bean.DictionaryArgsType
import com.gsq.iart.data.bean.DictionaryWorksBean
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.visible

class DictionaryWorksAdapter constructor(var listener: CallBackListener) :
    BaseQuickAdapter<DictionaryWorksBean, BaseViewHolder>(R.layout.item_dictionary_works_layout) {

    var compareList: ArrayList<DictionaryWorksBean>
    private var args: DictionaryArgsType? = null
    var isEdit: Boolean = false

    init {
        setAdapterAnimation(SettingUtil.getListMode())
        addChildClickViewIds(R.id.iv_contrast, R.id.iv_delete, R.id.item_works_cover)
        compareList = CacheUtil.getCompareList()
    }

    interface CallBackListener {
        fun loadMore()
    }

    fun setArgsType(args: DictionaryArgsType) {
        this.args = args
    }

    fun updateCompareList() {
        compareList = CacheUtil.getCompareList()
    }

    override fun convert(holder: BaseViewHolder, item: DictionaryWorksBean) {
        LogUtils.dTag("WorksAdapter", "layoutPosition:${holder.layoutPosition}")
        var contrastBtn = holder.getView<ImageView>(R.id.iv_contrast)
        holder.setText(R.id.item_works_name, item.name)
        holder.setText(R.id.item_works_source, "来源：[${item.mainAge}]${item.mainName}")
        var vipIcon = holder.getView<ImageView>(R.id.icon_vip)
        var imageView = holder.getView<ShapeableImageView>(R.id.item_works_cover)
        var layoutParams = imageView.layoutParams
        layoutParams.height = (ScreenUtils.getScreenWidth()-SizeUtils.dp2px(44f))/2
        if (item.thumbWidth > 0 && item.thumbHeight > 0) {
            var biliary = item.thumbWidth.toFloat() / item.thumbHeight.toFloat()
            LogUtils.dTag("WorksAdapter", "name:${item.name},biliary:$biliary")
        }
//        imageView.setImageViewRatio(item.thumbWidth, item.thumbHeight)
        GlideHelper.load(imageView, item.thumb, R.color.color_DDDDDD)
        if (args?.firstTag == COMPLEX_TYPE_NATIVE_COMPARE || args?.firstTag == COMPLEX_TYPE_COMPARE) {
            item.isAddCompare = true
            contrastBtn.gone()
        } else {
//            contrastBtn.visible()
            args?.dictionarySetsBean?.let {
                Constant.compareItemPageData?.let { compareList ->
                    if (compareList.find { it.workId == item.workId } != null) {
                        item.isAddCompare = true
//                        contrastBtn.gone()
                        contrastBtn.setImageResource(R.drawable.compare_remove)
                    } else {
//                        contrastBtn.visible()
                        contrastBtn.setImageResource(R.drawable.icon_add)
                        item.isAddCompare = false
                    }
                }
            }?:let {
                contrastBtn.visible()
                if (compareList.find { it.workId == item.workId } != null) {
                    item.isAddCompare = true
                    contrastBtn.setImageResource(R.drawable.compare_remove)
                } else {
                    contrastBtn.setImageResource(R.drawable.icon_add)
                    item.isAddCompare = false
                }
            }
        }

        if (holder.layoutPosition + 4 == data.size && CacheUtil.getUserVipStatus() == 99 && args?.firstTag != COMPLEX_TYPE_NATIVE_COMPARE) {
            listener.loadMore()
//            EventBus.getDefault().post(LoadMoreEvent(true))
        }
        if (item.pay == 1) {
            vipIcon.visible()
        } else {
            vipIcon.gone()
        }
        if (isEdit) {
            holder.getView<ImageView>(R.id.iv_delete).visible()
        } else {
            holder.getView<ImageView>(R.id.iv_delete).gone()
        }
    }
}