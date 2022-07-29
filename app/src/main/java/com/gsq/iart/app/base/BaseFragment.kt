package com.gsq.iart.app.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gsq.iart.app.ext.dismissLoadingExt
import com.gsq.iart.app.ext.hideSoftKeyboard
import com.gsq.iart.app.ext.showLoadingExt
import com.gsq.iart.app.weight.CustomLoadMoreView
import com.gsq.mvvm.base.fragment.BaseVmVbFragment
import com.gsq.mvvm.base.viewmodel.BaseViewModel

/**
 * 描述　: 你项目中的Fragment基类，在这里实现显示弹窗，吐司，还有自己的需求操作 ，如果不想用Databind，请继承
 * BaseVmFragment例如
 * abstract class BaseFragment<VM : BaseViewModel> : BaseVmFragment<VM>() {
 */
abstract class BaseFragment<VM : BaseViewModel, VB : ViewBinding> : BaseVmVbFragment<VM, VB>() {

    abstract override fun initView(savedInstanceState: Bundle?)

    /**
     * 懒加载 只有当前fragment视图显示时才会触发该方法
     */
    override fun lazyLoadData() {}

    /**
     * 创建LiveData观察者 Fragment执行onViewCreated后触发
     */
    override fun createObserver() {}

    /**
     * Fragment执行onViewCreated后触发
     */
    override fun initData() {

    }

    /**
     * 打开等待框
     */
    override fun showLoading(message: String) {
        showLoadingExt(message)
    }

    /**
     * 关闭等待框
     */
    override fun dismissLoading() {
        dismissLoadingExt()
    }

    override fun onPause() {
        super.onPause()
        hideSoftKeyboard(activity)
    }

    /**
     * 延迟加载 防止 切换动画还没执行完毕时数据就已经加载好了，这时页面会有渲染卡顿  bug
     * 这里传入你想要延迟的时间，延迟时间可以设置比转场动画时间长一点 单位： 毫秒
     * 不传默认 300毫秒
     * @return Long
     */
    override fun lazyLoadTime(): Long {
        return 300
    }

    open fun onLoadMore() {

    }

    open fun <T> BaseQuickAdapter<T, BaseViewHolder>.init(
        recyclerView: RecyclerView?,
        isEnableLoadMore: Boolean = false
    ): BaseQuickAdapter<T, BaseViewHolder> {
        if (isEnableLoadMore) {
            val loadMoreModule = loadMoreModule
            loadMoreModule.isEnableLoadMore = true
            val customLoadMoreView = CustomLoadMoreView()
            loadMoreModule.loadMoreView = customLoadMoreView
            loadMoreModule.preLoadNumber = 10
            loadMoreModule.setOnLoadMoreListener {
                onLoadMore()
            }
        }
        recyclerView?.adapter = this
        return this
    }

    protected var mFirstPage = true
    open fun <T> BaseQuickAdapter<T, BaseViewHolder>.loadData(
        list: List<T>?,
        emptyView: View? = null,
        hasNext: Int = 0,
        showBottomView: Boolean = true
    ) {
        if (mFirstPage) {
            setData(list, emptyView, hasNext, showBottomView)
        } else {
            addData(list, hasNext, showBottomView)
        }
    }

    fun <T> BaseQuickAdapter<T, BaseViewHolder>.setData(
        list: List<T>?,
        emptyView: View? = null,
        hasNext: Int = 0,
        showBottomView: Boolean = true
    ) {
        mFirstPage = false
        setList(list)
        if (list.isNullOrEmpty()) {
            if (data.isNullOrEmpty()) {//之前的adapter中没有数据
                if (headerLayoutCount > 0) {
                    emptyView?.let {
                        removeAllFooterView()
                        if (it.parent != null) {
                            (it.parent as ViewGroup).removeView(it)
                        }
                        addFooterView(it)
                    }
                } else {
                    emptyView?.let {
                        if (emptyView.parent != null) {
                            (emptyView.parent as ViewGroup).removeView(emptyView)
                        }
                        setEmptyView(emptyView)
                    }
                }
            }
        } else {
            if (footerLayoutCount > 0) {
                removeAllFooterView()
            }
            if (hasNext == 1) {
                loadMoreModule.loadMoreComplete()
            } else {
                loadMoreModule.loadMoreEnd(!showBottomView)
            }
        }
    }

    fun <T> BaseQuickAdapter<T, BaseViewHolder>.addData(
        list: List<T>?,
        hasNext: Int = 0,
        showBottomView: Boolean = true
    ) {
        mFirstPage = false
        if (list == null) {
            loadMoreModule.loadMoreFail()
        } else {
            addData(list)
            if (hasNext == 1) {
                loadMoreModule.loadMoreComplete()
            } else {
                loadMoreModule.loadMoreEnd(!showBottomView)
            }
        }
    }
}