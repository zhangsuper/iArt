package com.gsq.iart.ui.fragment.home

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.*
import com.gsq.iart.data.bean.HomeClassifyBean
import com.gsq.iart.databinding.FragmentWorksListBinding
import com.gsq.iart.ui.adapter.WorksAdapter
import com.gsq.iart.viewmodel.WorksViewModel
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.fragment_works_list.*

/**
 * 作品列表
 */
class WorksListFragment: BaseFragment<WorksViewModel, FragmentWorksListBinding>() {

    //适配器
    private val worksAdapter: WorksAdapter by lazy { WorksAdapter() }
    //界面状态管理者
    private lateinit var loadsir: LoadService<Any>

    private var classifyBean: HomeClassifyBean? = null
    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            classifyBean = it.getSerializable("classifyBean") as? HomeClassifyBean?
        }
        classifyBean?.let {
            text_content.text = it.name
        }
        //状态页配置
        loadsir = loadServiceInit(works_recycler_view) {
            //点击重试时触发的操作
            loadsir.showLoading()
            requestData(true)
        }
        //初始化recyclerView和Adapter
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//        works_recycler_view.layoutManager = layoutManager
//        worksAdapter.init(works_recycler_view, true)
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
        classifyBean?.id?.let{
            mViewModel.getWorksListData(isRefresh, it)
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.worksDataState.observe(viewLifecycleOwner, Observer {
            //设值 新写了个拓展函数
            loadListData(it, worksAdapter, loadsir, works_recycler_view, null)
        })
    }

    companion object {
        fun newInstance(classifyBean: HomeClassifyBean): WorksListFragment {
            val args = Bundle()
            args.putSerializable("classifyBean",classifyBean)
            val fragment = WorksListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}