package com.gsq.iart.ui.fragment.dictionary

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.mvrx.args
import com.airbnb.mvrx.asMavericksArgs
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.*
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.weight.FlowContentLayout.ClickListener
import com.gsq.iart.data.Constant
import com.gsq.iart.data.Constant.COMPLEX_TYPE_DICTIONARY
import com.gsq.iart.data.bean.DictionaryArgsType
import com.gsq.iart.data.bean.DictionaryMenuBean
import com.gsq.iart.data.bean.WorksBean
import com.gsq.iart.databinding.FragmentDictionarySubListBinding
import com.gsq.iart.ui.adapter.DictionaryLevelAdapter
import com.gsq.iart.ui.adapter.DictionaryWorksAdapter
import com.gsq.iart.ui.adapter.SearchHotAdapter
import com.gsq.iart.ui.fragment.mine.MemberFragment
import com.gsq.iart.viewmodel.DictionaryViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.visible
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.fragment_dictionary_sub_list.fourth_recycler_view
import kotlinx.android.synthetic.main.fragment_dictionary_sub_list.line_view
import kotlinx.android.synthetic.main.fragment_search_init.search_historyRv
import kotlinx.android.synthetic.main.fragment_search_init.search_hotRv
import kotlinx.android.synthetic.main.fragment_works_list.*

/**
 * 图典作品列表
 */
class DictionarySubListFragment : BaseFragment<DictionaryViewModel, FragmentDictionarySubListBinding>() {

    private val args: DictionaryArgsType by args()
    private var subTitleList: MutableList<DictionaryMenuBean>? = null


    //适配器
    private val worksAdapter: DictionaryWorksAdapter by lazy {
        DictionaryWorksAdapter(object : DictionaryWorksAdapter.CallBackListener {
            override fun loadMore() {
                requestData()
            }
        })
    }

    private val fourTagAdapter: DictionaryLevelAdapter by lazy { DictionaryLevelAdapter(arrayListOf()) }

    //界面状态管理者
    private lateinit var loadsir: LoadService<Any>
    private var tag3:String = ""
    private var tag4:String = ""


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
        works_recycler_view.init(layoutManager, worksAdapter)
        works_recycler_view.initFooter {
            //加载更多
            requestData()
        }

        //创建流式布局layout
        val layoutManager2 = FlexboxLayoutManager(context)
        //方向 主轴为水平方向，起点在左端
        layoutManager2.flexDirection = FlexDirection.ROW
        //左对齐
        layoutManager2.justifyContent = JustifyContent.FLEX_START
        fourth_recycler_view.init(layoutManager2, fourTagAdapter, false)

        worksAdapter.setOnItemClickListener { adapter, view, position ->
            val worksBean = adapter.data[position] as WorksBean
            if (worksBean.pay == 1 && CacheUtil.getUser()?.memberType != 1) {
                //需要付费且没有开通了会员
                nav().navigateAction(
                    R.id.action_mainFragment_to_memberFragment,
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
        mViewBind.flowContentLayout.setOnclickListener(object : ClickListener{
            override fun onClick(tag: String?) {
                tag?.let {
                    tag3 = it
                }
                if(tag.isNullOrEmpty()) {
                    fourth_recycler_view.gone()
                    line_view.gone()
                }else{
                    subTitleList?.let {
                        it.find { it.name == tag }.let {
                            it?.id?.let {
                                mViewModel.getDictionaryFourClassifyById(it)
                            }
                        }
                    }
                }
                requestData(true)
            }
        })
        fourTagAdapter.setOnItemClickListener { adapter, view, position ->
            if(fourTagAdapter.selectedPosition == position){
                fourTagAdapter.setSelectedPosition(-1)
                tag4 = ""
            }else {
                fourTagAdapter.setSelectedPosition(position)
                tag4 = fourTagAdapter.data[position].name
            }
            requestData(true)
        }
    }



    override fun lazyLoadData() {
        super.lazyLoadData()
        loadsir.showLoading()
        args.pid?.let {
            mViewModel.getDictionaryClassifyById(it)
        }
        requestData(true)
    }

    override fun onLoadMore() {
        super.onLoadMore()
        requestData()
    }

    /**
     * 获取列表数据
     */
    private fun requestData(isRefresh: Boolean = false) {
        mViewModel.getDictionaryWorks(isRefresh,"",args.firstTag?: "",args.tag?:"",tag3,tag4)
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.classifySubList.observe(viewLifecycleOwner){
            it?.let { subList ->
                var list = mutableListOf<String>()
                subList.forEach {
                    list.add(it.name)
                }
                subTitleList = subList
                mViewBind.flowContentLayout.addViews(list)
            }
        }
        mViewModel.classifyFourSubList.observe(viewLifecycleOwner){
            it?.let { subList ->
//                var list = mutableListOf<String>()
//                subList.forEach {
//                    list.add(it.name)
//                }
                fourTagAdapter.data = subList
                if(subList.size>0) {
                    fourth_recycler_view.visible()
                    line_view.visible()
                }else{
                    fourth_recycler_view.gone()
                    line_view.gone()
                }
            }?: let {
                fourth_recycler_view.gone()
                line_view.gone()
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