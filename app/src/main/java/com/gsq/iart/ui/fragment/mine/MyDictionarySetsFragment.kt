package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.gsq.iart.R
import com.gsq.iart.app.App
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.init
import com.gsq.iart.app.ext.initFooter
import com.gsq.iart.app.ext.loadListData
import com.gsq.iart.app.ext.loadServiceInit
import com.gsq.iart.app.ext.showLoading
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.data.Constant
import com.gsq.iart.data.bean.DictionarySetsBean
import com.gsq.iart.data.event.CompareDeleteEvent
import com.gsq.iart.data.event.CompareItemAddEvent
import com.gsq.iart.data.event.CompareItemDeleteEvent
import com.gsq.iart.data.event.CompareRenameEvent
import com.gsq.iart.databinding.FragmentDictionarySetsBinding
import com.gsq.iart.ui.adapter.DictionarySetsAdapter
import com.gsq.iart.viewmodel.DictionaryViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.fragment_my_collect.title_layout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MyDictionarySetsFragment :
    BaseFragment<DictionaryViewModel, FragmentDictionarySetsBinding>() {

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
            var bean = adapter.data[position] as DictionarySetsBean
            var bundle = Bundle()
            bundle.putString(Constant.INTENT_TYPE, Constant.COMPLEX_TYPE_COMPARE)
            bundle.putSerializable(Constant.INTENT_DATA, bean)
            nav().navigateAction(
                R.id.action_myDictionarySetsFragment_to_compareListFragment,
                bundle
            )
        }
        EventBus.getDefault().register(this)
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        loadsir.showLoading()
        requestData(true)
    }

    private fun requestData(isRefresh: Boolean = false) {
        mViewModel.findComparePage(isRefresh)
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.comparePageDataState.observe(viewLifecycleOwner) {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: CompareRenameEvent) {
        mAdapter.data.find { it.id == event.id }?.name = event.name
        mAdapter.notifyDataSetChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: CompareDeleteEvent) {
        var bean = mAdapter.data.find { it.id == event.id }
        bean?.let {
            mAdapter.remove(it)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: CompareItemDeleteEvent) {
        requestData(true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: CompareItemAddEvent) {
        requestData(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}