package com.gsq.iart.ui.fragment.search

import android.os.Bundle
import androidx.lifecycle.Observer
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.init
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.databinding.FragmentSearchInitBinding
import com.gsq.iart.ui.adapter.SearchHistoryAdapter
import com.gsq.iart.ui.adapter.SearchHotAdapter
import com.gsq.iart.viewmodel.SearchViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.parseState
import com.gsq.mvvm.ext.util.toJson
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search_init.*

/**
 * 搜索页
 */
class SearchInitFragment: BaseFragment<SearchViewModel, FragmentSearchInitBinding>() {


    private val historyAdapter: SearchHistoryAdapter by lazy { SearchHistoryAdapter(arrayListOf()) }

    private val hotAdapter: SearchHotAdapter by lazy { SearchHotAdapter(arrayListOf()) }

    override fun initView(savedInstanceState: Bundle?) {
        //创建流式布局layout
        val layoutManager1 = FlexboxLayoutManager(context)
        //方向 主轴为水平方向，起点在左端
        layoutManager1.flexDirection = FlexDirection.ROW
        //左对齐
        layoutManager1.justifyContent = JustifyContent.FLEX_START
        val layoutManager2 = FlexboxLayoutManager(context)
        //方向 主轴为水平方向，起点在左端
        layoutManager2.flexDirection = FlexDirection.ROW
        //左对齐
        layoutManager2.justifyContent = JustifyContent.FLEX_START
        //初始化搜搜历史Recyclerview
        search_historyRv.init(layoutManager1, historyAdapter, false)
        search_hotRv.init(layoutManager2, hotAdapter, false)

        search_clear.setOnClickListener {
            mViewModel.searchHistoryList.value = arrayListOf()
        }

        historyAdapter.run {
            setOnItemClickListener { adapter, view, position ->
                val queryStr = historyAdapter.data[position]
                updateKey(queryStr)
                searchData(queryStr)
            }
        }

        hotAdapter.run {
            setOnItemClickListener { adapter, view, position ->
                val queryStr = hotAdapter.data[position].name
                updateKey(queryStr)
                searchData(queryStr)
            }
        }
    }

    override fun lazyLoadData() {
        //获取历史搜索词数据
        mViewModel.getHistoryData()
        //获取热门数据
        mViewModel.getHotData()
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.searchHistoryList.observe(viewLifecycleOwner, Observer {
            historyAdapter.data = it
            historyAdapter.notifyDataSetChanged()
            CacheUtil.setSearchHistoryData(it.toJson())
        })

        mViewModel.searchHotList.observe(viewLifecycleOwner, Observer { resultState ->
            parseState(resultState, {
                hotAdapter.setList(it)
            })
        })
    }

    /**
     * 更新搜索词
     */
    fun updateKey(keyStr: String) {
        mViewModel.searchHistoryList.value?.let {
            if (it.contains(keyStr)) {
                //当搜索历史中包含该数据时 删除
                it.remove(keyStr)
            } else if (it.size >= 10) {
                //如果集合的size 有10个以上了，删除最后一个
                it.removeAt(it.size - 1)
            }
            //添加新数据到第一条
            it.add(0, keyStr)
            mViewModel.searchHistoryList.value = it
        }
    }

    fun searchData(keyStr: String){
        onClickItemListener?.invoke(keyStr)
    }

    private var onClickItemListener: ((key: String) -> Unit)? = null

    fun setOnClickItemListener(listener: ((key: String) -> Unit)?): SearchInitFragment {
        this.onClickItemListener = listener
        return this
    }
}