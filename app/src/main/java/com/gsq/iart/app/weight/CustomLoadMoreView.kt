package com.gsq.iart.app.weight

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.loadmore.LoadMoreStatus
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gsq.iart.R
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.visible


/**
 * 自定义加载更多
 */
class CustomLoadMoreView : BaseLoadMoreView() {

    private var view_loading: ProgressBar? = null

    override fun getRootView(parent: ViewGroup): View {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_load_more, parent, false)
        view_loading = view.findViewById(R.id.view_load_more_loading)
        return view
    }

    override fun getLoadingView(holder: BaseViewHolder): View {
        return holder.getView(R.id.view_load_more_loading)
    }

    override fun getLoadComplete(holder: BaseViewHolder): View {
        return holder.getView(R.id.view_load_complete)
    }

    override fun getLoadFailView(holder: BaseViewHolder): View {
        return holder.getView(R.id.view_load_fail)
    }

    override fun getLoadEndView(holder: BaseViewHolder): View {
        return holder.getView(R.id.view_load_end)
    }


    override fun convert(holder: BaseViewHolder, position: Int, loadMoreStatus: LoadMoreStatus) {
        super.convert(holder, position, loadMoreStatus)
        when (loadMoreStatus) {
            LoadMoreStatus.Loading -> view_loading?.visible()
            else -> view_loading?.gone()
        }
    }

}