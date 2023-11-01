package com.gsq.iart.ui.fragment.home

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
import com.gsq.iart.BuildConfig
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
import com.gsq.iart.data.bean.WorksBean
import com.gsq.iart.data.event.BigImageClickEvent
import com.gsq.iart.data.request.WorkPropSearchBean
import com.gsq.iart.databinding.FragmentWorksListBinding
import com.gsq.iart.ui.adapter.WorksAdapter
import com.gsq.iart.ui.dialog.AllConditionPopupWindow
import com.gsq.iart.ui.dialog.ConditionPopupWindow
import com.gsq.iart.ui.fragment.mine.MemberFragment
import com.gsq.iart.viewmodel.WorksViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.onClick
import com.gsq.mvvm.ext.view.visible
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.fragment_dictionary_sub_list.open_vip_btn
import kotlinx.android.synthetic.main.fragment_work_detail.*
import kotlinx.android.synthetic.main.fragment_works_list.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 作品列表
 */
class WorksListFragment : BaseFragment<WorksViewModel, FragmentWorksListBinding>() {

    private val args: ArgsType by args()

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
    private var subType: Int = WORKS_SUB_TYPE_HOT//0：热门  1：新上

    private var classifyBean: MutableList<ConditionClassifyBean>? = null
    private var selectType: String = "年代"
    private var searchKey: String? = null
    private var propSearchMap = hashMapOf<String, MutableList<ConditionClassifyBean>>()
    private var mScrollState = -1

    override fun initView(savedInstanceState: Bundle?) {
        if (args.complexType == COMPLEX_TYPE_SEARCH) {
            searchKey = args.searchKey
        }
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
            if(CacheUtil.getUser()?.memberType != 1 && BuildConfig.DEBUG) {
                works_recycler_view.loadMoreFinish(false,true)
            }else{
                requestData()
            }
        }

        initTypeConditionView()

        worksAdapter.setOnItemClickListener { adapter, view, position ->
            val worksBean = adapter.data[position] as WorksBean
            var eventMap = mutableMapOf<String, Any?>()
            eventMap["work_id"] = worksBean.id
            when (args.complexType) {
                COMPLEX_TYPE_SEARCH -> {
                    MobAgentUtil.onEvent("click_s", eventMap)
                }
                COMPLEX_TYPE_COLLECT -> {
                    MobAgentUtil.onEvent("click", eventMap)
                }
                else -> {
                    MobAgentUtil.onEvent("click", eventMap)
                }
            }
            if (worksBean.pay == 1 && CacheUtil.getUser()?.memberType != 1) {
//                if (CacheUtil.isLogin()) {
//                    if (CacheUtil.getUser()?.memberType != 1) {
//                        nav().navigateAction(R.id.action_mainFragment_to_memberFragment)
//                    } else {
//                        var bundle = Bundle()
//                        bundle.putSerializable(
//                            DATA_WORK,
//                            (adapter.data as MutableList<WorksBean>)[position]
//                        )
//                        bundle.putString(INTENT_TYPE, args.complexType)
//                        nav().navigateAction(R.id.action_mainFragment_to_workDetailFragment, bundle)
//                    }
//                } else {
//                    nav().navigateAction(R.id.action_mainFragment_to_loginFragment)
//                }
                //需要付费且没有开通了会员
                nav().navigateAction(
                    R.id.action_mainFragment_to_memberFragment,
                    bundleOf(MemberFragment.INTENT_KEY_TYPE to MemberFragment.INTENT_VALUE_WORKS)
                )
            } else {
                var bundle = Bundle()
                bundle.putSerializable(
                    DATA_WORK,
                    (adapter.data as MutableList<WorksBean>)[position]
                )
                bundle.putString(INTENT_TYPE, args.complexType)
                nav().navigateAction(R.id.action_mainFragment_to_workDetailFragment, bundle)
            }
        }

        open_vip_btn.onClick {
            //开通超级会员
        }
    }

    private fun initTypeConditionView() {
        if (args.complexType == COMPLEX_TYPE_COLLECT) {
            condition_view.gone()
        }
        hot_tab.setOnClickListener {
            //点击热门
            if (subType == WORKS_SUB_TYPE_HOT) {
                return@setOnClickListener
            }
            subType = WORKS_SUB_TYPE_HOT
            hot_tab.setTextColor(resources.getColor(R.color.color_141414))
            hot_tab.typeface = Typeface.DEFAULT_BOLD
            new_tab.setTextColor(resources.getColor(R.color.color_999999))
            new_tab.typeface = Typeface.DEFAULT
            hot_tab_indicator.visible()
            new_tab_indicator.gone()
            requestData(true)
            when (args.complexType) {
                COMPLEX_TYPE_SEARCH -> {
                    MobAgentUtil.onEvent("hot_guohua_hot")
                }
                COMPLEX_TYPE_COLLECT -> {

                }
                else -> {
                    MobAgentUtil.onEvent("hot_guohua")
                }
            }
        }
        new_tab.setOnClickListener {
            //点击最新
            if (subType == WORKS_SUB_TYPE_NEW) {
                return@setOnClickListener
            }
            subType = WORKS_SUB_TYPE_NEW
            hot_tab.setTextColor(resources.getColor(R.color.color_999999))
            hot_tab.typeface = Typeface.DEFAULT
            new_tab.setTextColor(resources.getColor(R.color.color_141414))
            new_tab.typeface = Typeface.DEFAULT_BOLD
            hot_tab_indicator.gone()
            new_tab_indicator.visible()
            requestData(true)
            when (args.complexType) {
                COMPLEX_TYPE_SEARCH -> {
                    MobAgentUtil.onEvent("new_guohua_new")
                }
                COMPLEX_TYPE_COLLECT -> {

                }
                else -> {
                    MobAgentUtil.onEvent("new_guohua")
                }
            }
        }
        condition_years_view.setOnClickListener {
            //年代
            selectType = classifyBean?.get(0)?.name ?: ""
            startAnimator(iv_years_frame, true)
            showConditionPopupWindow(classifyBean?.get(0)?.subs)
            when (args.complexType) {
                COMPLEX_TYPE_SEARCH -> {
                    MobAgentUtil.onEvent("filter_1_guohua_s")
                }
                COMPLEX_TYPE_COLLECT -> {

                }
                else -> {
                    MobAgentUtil.onEvent("filter_1_guohua")
                }
            }
        }
        condition_theme_view.setOnClickListener {
            //题材
            selectType = classifyBean?.get(1)?.name ?: ""
            startAnimator(iv_theme_frame, true)
            showConditionPopupWindow(classifyBean?.get(1)?.subs)
            when (args.complexType) {
                COMPLEX_TYPE_SEARCH -> {
                    MobAgentUtil.onEvent("filter_2_guohua_s")
                }
                COMPLEX_TYPE_COLLECT -> {

                }
                else -> {
                    MobAgentUtil.onEvent("filter_2_guohua")
                }
            }
        }
        condition_size_view.setOnClickListener {
            //尺寸
            selectType = classifyBean?.get(2)?.name ?: ""
            startAnimator(iv_size_frame, true)
            showConditionPopupWindow(classifyBean?.get(2)?.subs)
            when (args.complexType) {
                COMPLEX_TYPE_SEARCH -> {
                    MobAgentUtil.onEvent("filter_3_guohua_s")
                }
                COMPLEX_TYPE_COLLECT -> {

                }
                else -> {
                    MobAgentUtil.onEvent("filter_3_guohua")
                }
            }
        }
        condition_screen_view.setOnClickListener {
            //筛选
            selectType = "筛选"
//            startAnimator(iv_screen_frame, true)
//            showConditionPopupWindow(classifyBean?.get(3)?.subs)
            //全部筛选条件弹窗
            classifyBean?.let {
                showAllConditionPopupWindow(it)
                when (args.complexType) {
                    COMPLEX_TYPE_SEARCH -> {
                        MobAgentUtil.onEvent("filter_more_guohua_s")
                    }
                    COMPLEX_TYPE_COLLECT -> {

                    }
                    else -> {
                        MobAgentUtil.onEvent("filter_more_guohua")
                    }
                }
            }

        }
    }

    private fun showConditionPopupWindow(list: List<ConditionClassifyBean>?) {
        var selectedItem: List<ConditionClassifyBean>? = null
        if (propSearchMap.containsKey(selectType)) {
            selectedItem = propSearchMap[selectType]
        }
        var popupWindow = ConditionPopupWindow(
            requireContext(),
            ViewGroup.LayoutParams.MATCH_PARENT,
            SizeUtils.dp2px(300f)
        )
        list?.let { popupWindow.setData(it, selectedItem) }
        popupWindow.showAsDropDown(condition_detail_view)
        popupWindow.onBackListener(object : ConditionPopupWindow.OnBackListener {
            override fun onItemClick(
                grade1Item: ConditionClassifyBean,
                grade2Item: ConditionClassifyBean?
            ) {
                when (selectType) {
                    classifyBean?.get(0)?.name -> {
                        if (grade2Item != null && grade2Item.id != -1) {
                            tv_years.text = grade2Item.name
                        } else {
                            tv_years.text = grade1Item.name
                        }
                        tv_years.setTextColor(ColorUtils.getColor(R.color.color_141414))
                        when (args.complexType) {
                            COMPLEX_TYPE_SEARCH -> {
                                MobAgentUtil.onEvent("filter_1_guohua_s_ok")
                            }
                            COMPLEX_TYPE_COLLECT -> {

                            }
                            else -> {
                                MobAgentUtil.onEvent("filter_1_guohua_ok")
                            }
                        }
                    }
                    classifyBean?.get(1)?.name -> {
                        if (grade2Item != null && grade2Item.id != -1) {
                            tv_theme.text = grade2Item.name
                        } else {
                            tv_theme.text = grade1Item.name
                        }
                        tv_theme.setTextColor(ColorUtils.getColor(R.color.color_141414))
                        when (args.complexType) {
                            COMPLEX_TYPE_SEARCH -> {
                                MobAgentUtil.onEvent("filter_2_guohua_s_ok")
                            }
                            COMPLEX_TYPE_COLLECT -> {

                            }
                            else -> {
                                MobAgentUtil.onEvent("filter_2_guohua_ok")
                            }
                        }
                    }
                    classifyBean?.get(2)?.name -> {
                        if (grade2Item != null && grade2Item.id != -1) {
                            tv_size.text = grade2Item.name
                        } else {
                            tv_size.text = grade1Item.name
                        }
                        tv_size.setTextColor(ColorUtils.getColor(R.color.color_141414))
                        when (args.complexType) {
                            COMPLEX_TYPE_SEARCH -> {
                                MobAgentUtil.onEvent("filter_3_guohua_s_ok")
                            }
                            COMPLEX_TYPE_COLLECT -> {

                            }
                            else -> {
                                MobAgentUtil.onEvent("filter_3_guohua_ok")
                            }
                        }
                    }
                }
                var selectedItem = mutableListOf<ConditionClassifyBean>()
                selectedItem.add(grade1Item)
                if (grade2Item != null) {
                    selectedItem.add(grade2Item)
                }
                propSearchMap[selectType] = selectedItem
                requestData(true)
            }

            override fun onDismiss() {
                when (selectType) {
                    classifyBean?.get(0)?.name -> {
                        startAnimator(iv_years_frame, false)
                    }
                    classifyBean?.get(1)?.name -> {
                        startAnimator(iv_theme_frame, false)
                    }
                    classifyBean?.get(2)?.name -> {
                        startAnimator(iv_size_frame, false)
                    }
                    "筛选" -> {
                        startAnimator(iv_screen_frame, false)
                    }
                }
            }

            override fun onReset() {
                //重置
                if (propSearchMap.containsKey(selectType)) {
                    propSearchMap.remove(selectType)
                }
                when (selectType) {
                    classifyBean?.get(0)?.name -> {
                        tv_years.text = classifyBean?.get(0)?.name
                        tv_years.setTextColor(ColorUtils.getColor(R.color.color_999999))
                    }
                    classifyBean?.get(1)?.name -> {
                        tv_theme.text = classifyBean?.get(1)?.name
                        tv_theme.setTextColor(ColorUtils.getColor(R.color.color_999999))
                    }
                    classifyBean?.get(2)?.name -> {
                        tv_size.text = classifyBean?.get(2)?.name
                        tv_size.setTextColor(ColorUtils.getColor(R.color.color_999999))
                    }
                    "筛选" -> {
                        tv_screen.text = "筛选"
                        tv_screen.setTextColor(ColorUtils.getColor(R.color.color_999999))
                    }
                }
//                initConditionView()
                requestData(true)
            }
        })
    }

    private fun showAllConditionPopupWindow(list: List<ConditionClassifyBean>?) {
        var selectedItem: List<ConditionClassifyBean>? = null
        if (propSearchMap.containsKey(selectType)) {
            selectedItem = propSearchMap[selectType]
        }
        var popupWindow = AllConditionPopupWindow(
            requireContext(),
            ScreenUtils.getScreenWidth() * 4 / 5,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        popupWindow.setShowingBackgroundAlpha(0.5f)
        list?.let { popupWindow.setData(it, selectedItem) }
        popupWindow.showPopupFromScreenRight(R.layout.fragment_works_list)
        popupWindow.onBackListener(object : AllConditionPopupWindow.OnBackListener {
            override fun onItemClick(
                selectItems: HashMap<String, MutableList<ConditionClassifyBean>>
            ) {
                propSearchMap.clear()
                selectItems.forEach {
                    propSearchMap.put(it.key, it.value)

                    if (classifyBean?.get(0)?.name == it.key) {
                        if (it.value.size == 2 && it.value[1].id != -1) {
                            tv_years.text = it.value[1].name
                        } else {
                            tv_years.text = it.value[0].name
                        }
                    }
                    if (classifyBean?.get(1)?.name == it.key) {
                        if (it.value.size == 2 && it.value[1].id != -1) {
                            tv_theme.text = it.value[1].name
                        } else {
                            tv_theme.text = it.value[0].name
                        }
                    }
                    if (classifyBean?.get(2)?.name == it.key) {
                        if (it.value.size == 2 && it.value[1].id != -1) {
                            tv_size.text = it.value[1].name
                        } else {
                            tv_size.text = it.value[0].name
                        }
                    }
                }
//                initConditionView()
                requestData(true)
                when (args.complexType) {
                    COMPLEX_TYPE_SEARCH -> {
                        MobAgentUtil.onEvent("filter_more_guohua_s_ok")
                    }
                    COMPLEX_TYPE_COLLECT -> {

                    }
                    else -> {
                        MobAgentUtil.onEvent("filter_more_guohua_ok")
                    }
                }
            }

            override fun onDismiss() {
            }

            override fun onReset() {
                propSearchMap.clear()
                initConditionView()
                requestData(true)
            }
        })
    }

    private fun initConditionView() {
        tv_years.text = classifyBean?.get(0)?.name
        tv_theme.text = classifyBean?.get(1)?.name
        tv_size.text = classifyBean?.get(2)?.name
        tv_screen.text = "筛选"
        tv_years.setTextColor(ColorUtils.getColor(R.color.color_999999))
        tv_theme.setTextColor(ColorUtils.getColor(R.color.color_999999))
        tv_size.setTextColor(ColorUtils.getColor(R.color.color_999999))
    }

    private fun startAnimator(targetView: View, isOpen: Boolean) {
        if (isOpen) {
            ObjectAnimator.ofFloat(targetView, "rotationX", 0f, 180f).start()
        } else {
            ObjectAnimator.ofFloat(targetView, "rotationX", 180f, 0f).start()
        }

    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        loadsir.showLoading()
        requestData(true)
        if (args.complexType != COMPLEX_TYPE_COLLECT)
            mViewModel.getConditionAllClassify()//获取过滤分类
    }

    override fun onLoadMore() {
        super.onLoadMore()
        requestData()
    }

    fun requestSearchData(searchKey: String) {
        this.searchKey = searchKey
        requestData(true)
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
        when (args.complexType) {
            COMPLEX_TYPE_SEARCH -> {
                searchKey?.let {
                    mViewModel?.getWorksListData(
                        isRefresh,
                        -1,
                        subType,
                        propSearchs,
                        searchKey = it
                    )
                }
            }
            COMPLEX_TYPE_COLLECT -> {
                mViewModel.collectWorks(isRefresh)
//                mViewModel.getWorksListData(
//                    isRefresh,
//                    -1,
//                    propSearchs = propSearchs,
//                    searchKey = "1111111"
//                )
            }
            else -> {
                args.classifyId?.let {
                    mViewModel.getWorksListData(isRefresh, it, subType, propSearchs)
                }
            }
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.worksDataState.observe(viewLifecycleOwner, Observer {
            //设值 新写了个拓展函数
            loadListData(
                it,
                worksAdapter,
                loadsir,
                works_recycler_view,
                works_refresh_layout,
                args.complexType
            )
            if(it.isRefresh && it.listData.size>8 && CacheUtil.getUser()?.memberType != 1 && BuildConfig.DEBUG){
                open_vip_btn.visible()
            }else{
                open_vip_btn.gone()
            }
        })
        mViewModel.conditionRootClassifys.observe(viewLifecycleOwner, Observer {
            if (it.isSuccess) {
                classifyBean = it.listData
                classifyBean?.let {
                    it.forEach { bean ->
                        bean.subs.forEach { subBean ->
                            if (subBean.subs != null && subBean.subs.isNotEmpty() && subBean.subs[0].id != -1) {
                                var bean = ConditionClassifyBean(
                                    id = -1,
                                    name = "全部",
                                    subs = mutableListOf(),
                                    dataSource = "",
                                    isSelected = false,
                                    searchField = ""
                                )
                                subBean.subs.add(0, bean)
                            }
                        }
//                        for (index in bean.subs.indices) {
//                            if (it[index].subs != null && it[index].subs.isNotEmpty() && it[index].subs[0].id != -1) {
//                                var bean = ConditionClassifyBean(
//                                    id = -1,
//                                    name = "全部",
//                                    subs = mutableListOf(),
//                                    dataSource = "",
//                                    isSelected = false,
//                                    searchField = ""
//                                )
//                                classifyBean?.get(index)?.subs?.add(0, bean)
//                            }
//                        }
                    }
                }
                initConditionView()
            } else {

            }
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: BigImageClickEvent?) {
        event?.let {
            if (content_view.isVisible) {
                content_view.gone()
            } else {
                content_view.visible()
            }
        }
    }

    companion object {

        const val TAG = "WorksListFragment"
        fun start(args: ArgsType): WorksListFragment {
            val fragment = WorksListFragment()
            fragment.arguments = args.asMavericksArgs()
            return fragment
        }
    }
}