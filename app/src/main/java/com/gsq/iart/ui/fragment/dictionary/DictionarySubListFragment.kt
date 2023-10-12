package com.gsq.iart.ui.fragment.dictionary

import android.animation.ObjectAnimator
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.mvrx.args
import com.airbnb.mvrx.asMavericksArgs
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.*
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.MobAgentUtil
import com.gsq.iart.data.Constant.COMPLEX_TYPE_COLLECT
import com.gsq.iart.data.Constant.COMPLEX_TYPE_SEARCH
import com.gsq.iart.data.Constant.DATA_WORK
import com.gsq.iart.data.Constant.INTENT_TYPE
import com.gsq.iart.data.Constant.WORKS_SUB_TYPE_HOT
import com.gsq.iart.data.Constant.WORKS_SUB_TYPE_NEW
import com.gsq.iart.data.bean.ArgsType
import com.gsq.iart.data.bean.ConditionClassifyBean
import com.gsq.iart.data.bean.DictionaryArgsType
import com.gsq.iart.data.bean.WorksBean
import com.gsq.iart.data.event.BigImageClickEvent
import com.gsq.iart.data.request.WorkPropSearchBean
import com.gsq.iart.databinding.FragmentDictionarySubListBinding
import com.gsq.iart.databinding.FragmentWorksListBinding
import com.gsq.iart.ui.adapter.DictionaryLevelAdapter
import com.gsq.iart.ui.adapter.SearchHistoryAdapter
import com.gsq.iart.ui.adapter.WorksAdapter
import com.gsq.iart.ui.dialog.AllConditionPopupWindow
import com.gsq.iart.ui.dialog.ConditionPopupWindow
import com.gsq.iart.ui.fragment.mine.MemberFragment
import com.gsq.iart.viewmodel.DictionaryViewModel
import com.gsq.iart.viewmodel.WorksViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.visible
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.fragment_dictionary_sub_list.level_three_recycler_view
import kotlinx.android.synthetic.main.fragment_search_init.search_historyRv
import kotlinx.android.synthetic.main.fragment_search_init.search_hotRv
import kotlinx.android.synthetic.main.fragment_work_detail.*
import kotlinx.android.synthetic.main.fragment_works_list.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 图典作品列表
 */
class DictionarySubListFragment : BaseFragment<DictionaryViewModel, FragmentDictionarySubListBinding>() {

    private val args: DictionaryArgsType by args()

    private val mThreeDictionaryAdapter: DictionaryLevelAdapter by lazy { DictionaryLevelAdapter(arrayListOf()) }

    //适配器
    private val worksAdapter: WorksAdapter by lazy {
        WorksAdapter(object : WorksAdapter.CallBackListener {
            override fun loadMore() {
                requestData()
            }
        })
    }

    //界面状态管理者
    private lateinit var loadsir: LoadService<Any>
    private var propSearchMap = hashMapOf<String, MutableList<ConditionClassifyBean>>()

    override fun initView(savedInstanceState: Bundle?) {

        //创建流式布局layout
        val layoutManager1 = FlexboxLayoutManager(context)
        //方向 主轴为水平方向，起点在左端
        layoutManager1.flexDirection = FlexDirection.ROW
        //左对齐
        layoutManager1.justifyContent = JustifyContent.FLEX_START
        //初始化搜搜历史Recyclerview
        level_three_recycler_view.init(layoutManager1, mThreeDictionaryAdapter, false)

//        var list = mutableListOf<String>()
//        if(args.tag != "all"){
//            list.add("测试1")
//            list.add("测试2")
//            list.add("测试3")
//            list.add("测试4")
//            list.add("测试5")
//            list.add("测试6")
//            list.add("测试7")
//            list.add("测试8")
//            list.add("测试9")
//            list.add("测试10")
//        }
//        mThreeDictionaryAdapter.data = list
        //状态页配置
        loadsir = loadServiceInit(works_refresh_layout) {
            //点击重试时触发的操作
            loadsir.showLoading()
            requestData(true)
        }
//        //初始化 SwipeRefreshLayout
//        works_refresh_layout.init {
//            //触发刷新监听时请求数据
//            requestData(true)
//        }
        //初始化recyclerView和Adapter
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//        works_recycler_view.addItemDecoration(
//            GridItemDecoration(
//                2,
//                SizeUtils.dp2px(12f),
//                includeEdge = true, hasTopBottomSpace = false
//            )
//        )
        works_recycler_view.init(layoutManager, worksAdapter)
        works_recycler_view.initFooter {
            //加载更多
            requestData()
        }
        mThreeDictionaryAdapter.setOnItemClickListener { adapter, view, position ->
            mThreeDictionaryAdapter.setSelectedPosition(position)
        }
    }



    override fun lazyLoadData() {
        super.lazyLoadData()
        loadsir.showLoading()
        requestData(true)
        args.pid?.let {
            mViewModel.getDictionaryClassifyById(it)
        }
    }

    override fun onLoadMore() {
        super.onLoadMore()
        requestData()
    }

    /**
     * 获取列表数据
     */
    private fun requestData(isRefresh: Boolean = false) {
        var propSearchs: MutableList<WorkPropSearchBean>? = null
        if (propSearchMap.size > 0) {
            propSearchs = mutableListOf()//多条件过滤
            propSearchMap.forEach { map ->
                if (map.value[map.value.size - 1].id == -1) {
                    if (map.value.size > 1) {
                        propSearchs.add(
                            WorkPropSearchBean(
                                map.value[map.value.size - 2].searchField,
                                map.value[map.value.size - 2].name
                            )
                        )
                    }
                } else {
                    propSearchs.add(
                        WorkPropSearchBean(
                            map.value[map.value.size - 1].searchField,
                            map.value[map.value.size - 1].name
                        )
                    )
                }
                if (map.value[map.value.size - 1].id != -1) {

                }
            }
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.classifySubList.observe(viewLifecycleOwner){
            it?.let { subList ->
//                var list = mutableListOf<String>()
//                subList.forEach {
//                    list.add(it.name)
//                }
                mThreeDictionaryAdapter.data = subList
                mThreeDictionaryAdapter.notifyDataSetChanged()
            }
        }
    }

    companion object {

        const val TAG = "DictionarySubListFragment"
        fun start(args: DictionaryArgsType): DictionarySubListFragment {
            val fragment = DictionarySubListFragment()
            fragment.arguments = args.asMavericksArgs()
            return fragment
        }
    }
}