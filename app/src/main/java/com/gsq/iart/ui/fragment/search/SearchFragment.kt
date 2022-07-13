package com.gsq.iart.ui.fragment.search

import android.os.Bundle
import android.text.TextUtils
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
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
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.parseState
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
        search_btn.setOnClickListener {
            var inputKey = search_input_view.text.toString()
            if(!TextUtils.isEmpty(inputKey)){
                updateKey(inputKey)
                searchData()
            }else{
                ToastUtils.showShort("请输入关键字")
            }
        }
        search_input_view.setOnEditorActionListener { textView, i, keyEvent ->{}
            if(i == EditorInfo.IME_ACTION_SEARCH) {
                searchData()
                true
            }
            false
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
    private fun updateKey(keyStr: String) {
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

    private fun searchData(){


    }

}