package com.gsq.iart.ui.fragment.search

import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.init
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.databinding.FragmentSearchBinding
import com.gsq.iart.ui.adapter.SearchHistoryAdapter
import com.gsq.iart.ui.adapter.SearchHotAdapter
import com.gsq.iart.viewmodel.SearchViewModel
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.util.toJson
import kotlinx.android.synthetic.main.fragment_search.*

/**
 * 搜索页
 */
class SearchFragment: BaseFragment<SearchViewModel, FragmentSearchBinding>() {

    private val historyAdapter: SearchHistoryAdapter by lazy { SearchHistoryAdapter(arrayListOf()) }

    private val hotAdapter: SearchHotAdapter by lazy { SearchHotAdapter(arrayListOf()) }

    override fun initView(savedInstanceState: Bundle?) {
        back_btn.setOnClickListener {
            nav().navigateUp()
        }
        //创建流式布局layout
        val layoutManager = FlexboxLayoutManager(context)
        //方向 主轴为水平方向，起点在左端
        layoutManager.flexDirection = FlexDirection.ROW
        //左对齐
        layoutManager.justifyContent = JustifyContent.FLEX_START
        //初始化搜搜历史Recyclerview
        search_historyRv.init(layoutManager, historyAdapter, false)
        search_hotRv.init(layoutManager, hotAdapter, false)
        search_btn.setOnClickListener {
            var inputKey = search_input_view.text.toString()
            if(!TextUtils.isEmpty(inputKey)){
                CacheUtil.setSearchHistoryData(inputKey)
            }else{
                ToastUtils.showShort("请输入关键字")
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

        mViewModel.searchHotList.observe(viewLifecycleOwner, Observer {

        })
    }

}