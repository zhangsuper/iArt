package com.gsq.iart.ui.fragment.home

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.mvrx.asMavericksArgs
import com.blankj.utilcode.util.SizeUtils
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.*
import com.gsq.iart.app.weight.recyclerview.GridItemDecoration
import com.gsq.iart.data.bean.ArgsType
import com.gsq.iart.data.bean.HomeClassifyBean
import com.gsq.iart.databinding.FragmentWorksListBinding
import com.gsq.iart.ui.adapter.WorksAdapter
import com.gsq.iart.viewmodel.WorksViewModel
import com.kingja.loadsir.core.LoadService
import com.airbnb.mvrx.args
import kotlinx.android.synthetic.main.fragment_works_list.*

/**
 * 作品列表
 */
class WorksListFragment: BaseFragment<WorksViewModel, FragmentWorksListBinding>() {

    private val args: ArgsType by args()
    //适配器
    private val worksAdapter: WorksAdapter by lazy { WorksAdapter() }
    //界面状态管理者
    private lateinit var loadsir: LoadService<Any>

    override fun initView(savedInstanceState: Bundle?) {
        //状态页配置
        loadsir = loadServiceInit(works_refresh_layout) {
            //点击重试时触发的操作
            loadsir.showLoading()
            requestData(true)
        }
        //初始化 SwipeRefreshLayout
        works_refresh_layout.init {
            //触发刷新监听时请求数据
            requestData(true)
        }
        //初始化recyclerView和Adapter
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        works_recycler_view.addItemDecoration(
            GridItemDecoration(
            2,
            SizeUtils.dp2px(12f),
            includeEdge = true, hasTopBottomSpace = true
            )
        )
        works_recycler_view.init(layoutManager,worksAdapter)
        works_recycler_view.initFooter {
            //加载更多
            requestData()
        }
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        loadsir.showLoading()
        requestData(true)
    }

    override fun onLoadMore() {
        super.onLoadMore()
        requestData()
    }

    private fun requestData(isRefresh: Boolean = false){
        args.classifyId?.let{
            mViewModel.getWorksListData(isRefresh, it)
        }
    }

    /**
     * 搜索作品
     */
    fun searchData(key: String){
        args.classifyId?.let{
            mViewModel?.getWorksListData(true, it)
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.worksDataState.observe(viewLifecycleOwner, Observer {
            //设值 新写了个拓展函数
            loadListData(it, worksAdapter, loadsir, works_recycler_view, works_refresh_layout)
        })
    }

    companion object {
//        fun newInstance(classifyBean: HomeClassifyBean): WorksListFragment {
//            val args = Bundle()
//            args.putSerializable("classifyBean",classifyBean)
//            val fragment = WorksListFragment()
//            fragment.arguments = args
//            return fragment
//        }

        fun start(args: ArgsType): WorksListFragment {
            val fragment = WorksListFragment()
            fragment.arguments = args.asMavericksArgs()
            return fragment
        }
    }
}