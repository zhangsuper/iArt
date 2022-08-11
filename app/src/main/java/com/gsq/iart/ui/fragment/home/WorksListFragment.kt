package com.gsq.iart.ui.fragment.home

import android.animation.ObjectAnimator
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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
import com.gsq.iart.R
import com.gsq.iart.data.Constant.COMPLEX_TYPE_COLLECT
import com.gsq.iart.data.Constant.COMPLEX_TYPE_SEARCH
import com.gsq.iart.data.Constant.DATA_WORK
import com.gsq.iart.data.Constant.WORKS_SUB_TYPE_HOT
import com.gsq.iart.data.Constant.WORKS_SUB_TYPE_NEW
import com.gsq.iart.data.bean.ConditionClassifyBean
import com.gsq.iart.data.bean.WorksBean
import com.gsq.iart.ui.dialog.ConditionPopupWindow
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.visible
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
    private var subType: Int = WORKS_SUB_TYPE_HOT//0：热门  1：新上

    private var classifyBean: List<ConditionClassifyBean>? = null
    private var selectType = 1

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

        initTypeConditionView()

        worksAdapter.setOnItemClickListener { adapter, view, position ->

            var args = Bundle()
            args.putSerializable(DATA_WORK, (adapter.data as MutableList<WorksBean>)[position])
            nav().navigateAction(R.id.action_mainFragment_to_workDetailFragment,args)
        }
    }

    private fun initTypeConditionView(){
        if(args.complexType == COMPLEX_TYPE_COLLECT){
            condition_view.gone()
        }
        hot_tab.setOnClickListener {
            //点击热门
            if(subType == WORKS_SUB_TYPE_HOT){
                return@setOnClickListener
            }
            subType = WORKS_SUB_TYPE_HOT
            hot_tab.setTextColor(resources.getColor(R.color.color_141414))
            hot_tab.typeface = Typeface.DEFAULT_BOLD
            new_tab.setTextColor(resources.getColor(R.color.color_888888))
            new_tab.typeface = Typeface.DEFAULT
            hot_tab_indicator.visible()
            new_tab_indicator.gone()
            requestData(true)
        }
        new_tab.setOnClickListener {
            //点击最新
            if(subType == WORKS_SUB_TYPE_NEW){
                return@setOnClickListener
            }
            subType = WORKS_SUB_TYPE_NEW
            hot_tab.setTextColor(resources.getColor(R.color.color_888888))
            hot_tab.typeface = Typeface.DEFAULT
            new_tab.setTextColor(resources.getColor(R.color.color_141414))
            new_tab.typeface = Typeface.DEFAULT_BOLD
            hot_tab_indicator.gone()
            new_tab_indicator.visible()
            requestData(true)
        }
        condition_years_view.setOnClickListener {
            //年代
            selectType =1
            startAnimator(iv_years_frame,true)
            showConditionPopupWindow(classifyBean?.get(0)?.subs)
        }
        condition_theme_view.setOnClickListener {
            //题材
            selectType =2
            startAnimator(iv_theme_frame,true)
            showConditionPopupWindow(classifyBean?.get(1)?.subs)
        }
        condition_size_view.setOnClickListener {
            //尺寸
            selectType =3
            startAnimator(iv_size_frame,true)
            showConditionPopupWindow(classifyBean?.get(2)?.subs)
        }
        condition_screen_view.setOnClickListener {
            //筛选
            selectType =4
            startAnimator(iv_screen_frame,true)
            showConditionPopupWindow(classifyBean?.get(3)?.subs)
        }
    }

    private fun showConditionPopupWindow(list: List<ConditionClassifyBean>?){
        var popupWindow = ConditionPopupWindow(requireContext(), ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(300f))
        list?.let { popupWindow.setData(it) }
        popupWindow.showAsDropDown(condition_detail_view)
        popupWindow.onBackListener(object : ConditionPopupWindow.OnBackListener{
            override fun onItemClick(
                grade1Item: ConditionClassifyBean,
                grade2Item: ConditionClassifyBean?
            ) {
                when(selectType){
                    1 ->{
                        if(grade2Item != null){
                            tv_years.text = grade2Item.name
                        }else{
                            tv_years.text = grade1Item.name
                        }
                    }
                    2 ->{
                        if(grade2Item != null){
                            tv_theme.text = grade2Item.name
                        }else{
                            tv_theme.text = grade1Item.name
                        }
                    }
                    3 ->{
                        if(grade2Item != null){
                            tv_size.text = grade2Item.name
                        }else{
                            tv_size.text = grade1Item.name
                        }
                    }
                    4 ->{
                        if(grade2Item != null){
                            tv_screen.text = grade2Item.name
                        }else{
                            tv_screen.text = grade1Item.name
                        }
                    }
                }
            }

            override fun onDismiss() {
                when(selectType){
                    1 ->{
                        startAnimator(iv_years_frame,false)
                    }
                    2 ->{
                        startAnimator(iv_theme_frame,false)
                    }
                    3 ->{
                        startAnimator(iv_size_frame,false)
                    }
                    4 ->{
                        startAnimator(iv_screen_frame,false)
                    }
                }
            }
        })
    }

    private fun startAnimator(targetView: View,isOpen: Boolean){
        if(isOpen){
            ObjectAnimator.ofFloat(targetView,"rotationX",0f,180f).start()
        }else{
            ObjectAnimator.ofFloat(targetView,"rotationX",180f,0f).start()
        }

    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        loadsir.showLoading()
        requestData(true)
        mViewModel.getConditionAllClassify()//获取过滤分类
    }

    override fun onLoadMore() {
        super.onLoadMore()
        requestData()
    }

    /**
     * 获取列表数据
     */
    private fun requestData(isRefresh: Boolean = false){
        when (args.complexType) {
            COMPLEX_TYPE_SEARCH -> {
                mViewModel.getWorksListData(isRefresh, 0)
            }
            COMPLEX_TYPE_COLLECT -> {
                mViewModel.getWorksListData(isRefresh, 0)
            }
            else -> {
                args.classifyId?.let {
                    mViewModel.getWorksListData(isRefresh, it, subType)
                }
            }
        }
    }


    /**
     * 搜索作品
     */
    fun searchData(key: String){
        args.classifyId?.let{
            mViewModel?.getWorksListData(true, it,searchKey = key)
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.worksDataState.observe(viewLifecycleOwner, Observer {
            //设值 新写了个拓展函数
            loadListData(it, worksAdapter, loadsir, works_recycler_view, works_refresh_layout)
        })
        mViewModel.conditionRootClassifys.observe(viewLifecycleOwner, Observer {
            if(it.isSuccess){
                classifyBean = it.listData
                tv_years.text = it.listData[0].name
                tv_theme.text = it.listData[1].name
                tv_size.text = it.listData[2].name
                tv_screen.text = it.listData[3].name
            }else{

            }
        })
    }

    companion object {
        fun start(args: ArgsType): WorksListFragment {
            val fragment = WorksListFragment()
            fragment.arguments = args.asMavericksArgs()
            return fragment
        }
    }
}