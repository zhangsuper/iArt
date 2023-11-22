package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.gsq.iart.R
import com.gsq.iart.app.App
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.init
import com.gsq.iart.app.ext.initFooter
import com.gsq.iart.app.ext.loadDictionaryListData
import com.gsq.iart.app.ext.loadListData
import com.gsq.iart.app.ext.loadServiceInit
import com.gsq.iart.app.ext.showEmpty
import com.gsq.iart.app.ext.showError
import com.gsq.iart.app.ext.showLoading
import com.gsq.iart.app.ext.showWorksCollectEmpty
import com.gsq.iart.app.ext.showWorksSearchEmpty
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.data.Constant
import com.gsq.iart.databinding.FragmentDictionarySetsBinding
import com.gsq.iart.ui.adapter.DictionarySetsAdapter
import com.gsq.iart.viewmodel.DictionaryViewModel
import com.gsq.mvvm.ext.nav
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.fragment_my_collect.title_layout
import kotlinx.android.synthetic.main.fragment_works_list.works_recycler_view
import kotlinx.android.synthetic.main.fragment_works_list.works_refresh_layout

class MyDictionarySetsFragment: BaseFragment<DictionaryViewModel, FragmentDictionarySetsBinding>() {

    private lateinit var mAdapter: DictionarySetsAdapter
    //界面状态管理者
    private lateinit var loadsir: LoadService<Any>

    override fun onResume() {
        super.onResume()
        StatusBarUtil.init(
            requireActivity(),
            fitSystem = true,
            statusBarColor = R.color.white,
            isDarkFont = true
        )
    }


    override fun initView(savedInstanceState: Bundle?) {
        title_layout.setBackListener {
            nav().navigateUp()
        }
        mAdapter = DictionarySetsAdapter()

        //状态页配置
        loadsir = loadServiceInit(mViewBind.refreshLayout) {
            //点击重试时触发的操作
            loadsir.showLoading()
            requestData(true)
        }
        //初始化 SwipeRefreshLayout
        mViewBind.refreshLayout.init {
            //触发刷新监听时请求数据
            requestData(true)
        }
        mViewBind.recyclerView.init(LinearLayoutManager(App.instance), mAdapter)
        mViewBind.recyclerView.initFooter {
            //加载更多
            requestData()
        }
        mAdapter.setOnItemClickListener { adapter, view, position ->

        }
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        loadsir.showLoading()
        requestData(true)
    }

    private fun requestData(isRefresh: Boolean = false){
        mViewModel.findComparePage(isRefresh)
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.comparePageDataState.observe(viewLifecycleOwner){
            it?.let {
                loadListData(
                    it,
                    mAdapter,
                    loadsir,
                    mViewBind.recyclerView,
                    mViewBind.refreshLayout,
                    ""
                )
            }
        }
    }

    override fun onLoadMore() {
        super.onLoadMore()
        requestData(false)
    }
}