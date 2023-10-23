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
import com.blankj.utilcode.util.ToastUtils
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.*
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.weight.recyclerview.GridItemDecoration
import com.gsq.iart.data.Constant
import com.gsq.iart.data.Constant.COMPLEX_TYPE_DICTIONARY
import com.gsq.iart.data.bean.DictionaryArgsType
import com.gsq.iart.data.bean.WorksBean
import com.gsq.iart.databinding.FragmentDictionarySubListBinding
import com.gsq.iart.ui.adapter.DictionaryLevelAdapter
import com.gsq.iart.ui.adapter.DictionaryWorksAdapter
import com.gsq.iart.ui.fragment.mine.MemberFragment
import com.gsq.iart.viewmodel.DictionaryViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.fragment_dictionary_sub_list.level_three_recycler_view
import kotlinx.android.synthetic.main.fragment_works_list.*

/**
 * 图典作品列表
 */
class DictionarySubListFragment : BaseFragment<DictionaryViewModel, FragmentDictionarySubListBinding>() {

    private val args: DictionaryArgsType by args()

    private val mThreeDictionaryAdapter: DictionaryLevelAdapter by lazy { DictionaryLevelAdapter(arrayListOf()) }

    //适配器
    private val worksAdapter: DictionaryWorksAdapter by lazy {
        DictionaryWorksAdapter(object : DictionaryWorksAdapter.CallBackListener {
            override fun loadMore() {
                requestData()
            }
        })
    }

    //界面状态管理者
    private lateinit var loadsir: LoadService<Any>
    private var tag3:String = ""


    override fun initView(savedInstanceState: Bundle?) {

        //创建流式布局layout
        val layoutManager1 = FlexboxLayoutManager(context)
        //方向 主轴为水平方向，起点在左端
        layoutManager1.flexDirection = FlexDirection.ROW
        //左对齐
        layoutManager1.justifyContent = JustifyContent.FLEX_START
        //初始化搜搜历史Recyclerview
        level_three_recycler_view.init(layoutManager1, mThreeDictionaryAdapter, false)

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
            if(mThreeDictionaryAdapter.selectedPosition == position){
                mThreeDictionaryAdapter.setSelectedPosition(-1)
                tag3 = ""
            }else{
                mThreeDictionaryAdapter.setSelectedPosition(position)
                //标签选择过滤
                var selectBean = mThreeDictionaryAdapter.data[position]
                tag3 = selectBean.name
            }
            requestData(true)
        }

        worksAdapter.setOnItemClickListener { adapter, view, position ->
            val worksBean = adapter.data[position] as WorksBean
            if (worksBean.pay == 1 && CacheUtil.getUser()?.memberType != 1) {
                //需要付费且没有开通了会员
                nav().navigateAction(
                    R.id.action_dictionaryListFragment_to_workDetailFragment,
                    bundleOf(MemberFragment.INTENT_KEY_TYPE to MemberFragment.INTENT_VALUE_WORKS)
                )
            } else {
                var bundle = Bundle()
                bundle.putSerializable(
                    Constant.DATA_WORK,
                    (adapter.data as MutableList<WorksBean>)[position]
                )
                bundle.putString(Constant.INTENT_TYPE, COMPLEX_TYPE_DICTIONARY)
                nav().navigateAction(R.id.action_dictionaryListFragment_to_workDetailFragment, bundle)
            }
        }
        worksAdapter.setClickContrastListener {
            //加入对比
            ToastUtils.showShort("加入对比")
        }
        var list = mutableListOf<String>()
        list.add("测试1")
        list.add("测试2")
        list.add("测试3")
        list.add("测试4")
        list.add("测试5")
        list.add("测试6")
        list.add("测试7")
        list.add("测试8")
        list.add("测试9")
        list.add("测试10")
        list.add("测试11")
        list.add("测试12")
        mViewBind.flowContentLayout.addViews(list)
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
        mViewModel.getDictionaryWorks(isRefresh,"",args.firstTag?: "",args.tag?:"",tag3,"")
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.classifySubList.observe(viewLifecycleOwner){
            it?.let { subList ->
                mThreeDictionaryAdapter.data = subList
                mThreeDictionaryAdapter.notifyDataSetChanged()
            }
        }
        mViewModel.worksDataState.observe(viewLifecycleOwner, Observer {
            //设值 新写了个拓展函数
            loadListData(
                it,
                worksAdapter,
                loadsir,
                works_recycler_view,
                works_refresh_layout,
                Constant.COMPLEX_TYPE_DICTIONARY
            )
        })
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