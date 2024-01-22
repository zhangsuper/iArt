package com.gsq.iart.ui.fragment.dictionary

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.mvrx.args
import com.airbnb.mvrx.asMavericksArgs
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.init
import com.gsq.iart.app.ext.initFooter
import com.gsq.iart.app.ext.loadDictionaryListData
import com.gsq.iart.app.ext.loadServiceInit
import com.gsq.iart.app.ext.showEmpty
import com.gsq.iart.app.ext.showLoading
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.weight.FlowContentLayout.ClickListener
import com.gsq.iart.data.Constant
import com.gsq.iart.data.Constant.COMPLEX_TYPE_DICTIONARY
import com.gsq.iart.data.bean.DictionaryArgsType
import com.gsq.iart.data.bean.DictionaryMenuBean
import com.gsq.iart.data.bean.DictionarySetsBean
import com.gsq.iart.data.bean.DictionaryWorksBean
import com.gsq.iart.data.event.CompareItemAddEvent
import com.gsq.iart.data.event.CompareItemDeleteEvent
import com.gsq.iart.databinding.FragmentDictionarySubListBinding
import com.gsq.iart.ui.adapter.DictionaryLevelAdapter
import com.gsq.iart.ui.adapter.DictionaryWorksAdapter
import com.gsq.iart.ui.fragment.mine.MemberFragment
import com.gsq.iart.viewmodel.DictionaryViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.onClick
import com.gsq.mvvm.ext.view.visible
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.fragment_dictionary_sub_list.fourth_recycler_view
import kotlinx.android.synthetic.main.fragment_dictionary_sub_list.line_view
import kotlinx.android.synthetic.main.fragment_dictionary_sub_list.open_vip_btn
import org.greenrobot.eventbus.EventBus

/**
 * 图典作品列表
 */
class DictionarySubListFragment :
    BaseFragment<DictionaryViewModel, FragmentDictionarySubListBinding>() {

    private val args: DictionaryArgsType by args()
    private var subTitleList: MutableList<DictionaryMenuBean>? = null
    var compareItemPageData: MutableList<DictionaryWorksBean>? = null


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
    private var tag3: String = ""
    private var tag4: String = ""
    private var selectedPosition = -1

    private var deleteIdPosition: Int? = null

    private var intent_data_sub: DictionarySetsBean? = null
    private var addComparePageItem: DictionaryWorksBean? = null//往图单新增的对象
    private var deleteComparePageItem: DictionaryWorksBean? = null//从图单移除的对象
    private var searchKey: String = ""

    fun setEditStatus(isEdit: Boolean) {
        worksAdapter.isEdit = isEdit
        worksAdapter.notifyDataSetChanged()
    }




    override fun initView(savedInstanceState: Bundle?) {
        //状态页配置
        loadsir = loadServiceInit(mViewBind.worksRefreshLayout) {
            //点击重试时触发的操作
            loadsir.showLoading()
            requestData(true)
        }
        //初始化 SwipeRefreshLayout
        mViewBind.worksRefreshLayout.init {
            //触发刷新监听时请求数据
            requestData(true)
        }
        //初始化recyclerView和Adapter
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mViewBind.worksRecyclerView.init(layoutManager, worksAdapter)
//        if (args.firstTag == Constant.COMPLEX_TYPE_NATIVE_COMPARE) {
//            mViewBind.worksRecyclerView.setAutoLoadMore(false)
//            mViewBind.worksRefreshLayout.isEnabled = false
//        }
        mViewBind.worksRecyclerView.initFooter {
            //加载更多
            if (CacheUtil.getUserVipStatus() != 99) {
                mViewBind.worksRecyclerView.loadMoreFinish(
                    false,
                    args.firstTag != Constant.COMPLEX_TYPE_NATIVE_COMPARE
                )
            } else {
                requestData()
            }
        }

        //创建流式布局layout
        val layoutManager2 = FlexboxLayoutManager(context)
        //方向 主轴为水平方向，起点在左端
        layoutManager2.flexDirection = FlexDirection.ROW
        //左对齐
        layoutManager2.justifyContent = JustifyContent.FLEX_START
        fourth_recycler_view.init(layoutManager2, fourTagAdapter, false)

        intent_data_sub = args.dictionarySetsBean

        worksAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.item_works_cover){
                val worksBean = adapter.data[position] as DictionaryWorksBean
                if (worksBean.pay == 1 && CacheUtil.getUserVipStatus() != 99) {
                    //需要付费且没有开通了会员
                    nav().navigateAction(
                        R.id.action_dictionaryListFragment_to_memberFragment,
                        bundleOf(MemberFragment.INTENT_KEY_TYPE to MemberFragment.INTENT_VALUE_DICTIONARY)
                    )
                } else {
                    var bundle = Bundle()
                    bundle.putSerializable(
                        Constant.DATA_WORK,
                        (adapter.data as MutableList<DictionaryWorksBean>)[position]
                    )
                    selectedPosition = position
                    bundle.putString(Constant.INTENT_TYPE, COMPLEX_TYPE_DICTIONARY)
                    if (args.firstTag == Constant.COMPLEX_TYPE_NATIVE_COMPARE || args.firstTag == Constant.COMPLEX_TYPE_COMPARE) {
                        nav().navigateAction(
                            R.id.action_compareListFragment_to_workDetailFragment,
                            bundle
                        )
                    } else {
                        nav().navigateAction(
                            R.id.action_dictionaryListFragment_to_workDetailFragment,
                            bundle
                        )
                    }
                }
            } else if (view.id == R.id.iv_contrast) {
                val worksBean = adapter.data[position] as DictionaryWorksBean
                if (worksBean.isAddCompare) {
                    intent_data_sub?.let {
                        //从图单列表删除
                        it.id?.let {
                            var list = mutableListOf<Int>()
                            list.add(worksBean.id)
                            deleteComparePageItem = worksBean
                            mViewModel.deleteCompareItems(it, list)
                        }
                    }?: let {
                        //本地删除
                        CacheUtil.removeCompare(worksBean)
                        ToastUtils.showShort("移除成功")
                        worksAdapter.updateCompareList()
                        worksAdapter.notifyDataSetChanged()
                    }
                } else {
                    //加入对比
                    intent_data_sub?.let {
                        var list = mutableListOf<Int>()
                        list.add(worksBean.id)
                        addComparePageItem = worksBean
                        mViewModel.addCompareItems(it.id, list)
                    }?: let{
                        CacheUtil.addCompareList(worksBean)
                        ToastUtils.showShort("加入成功")
                        worksAdapter.updateCompareList()
                        worksAdapter.notifyDataSetChanged()
                    }
                }
            } else if (view.id == R.id.iv_delete) {
                //删除
                val worksBean = adapter.data[position] as DictionaryWorksBean
                if (args.firstTag == Constant.COMPLEX_TYPE_COMPARE) {
                    //图单列表删除
                    args.dictionarySetId?.let {
                        var list = mutableListOf<Int>()
                        list.add(worksBean.id)
                        deleteIdPosition = position
                        mViewModel.deleteCompareItems(it, list)
                    }
                } else if (args.firstTag == Constant.COMPLEX_TYPE_NATIVE_COMPARE) {
                    //本地对比列表删除
                    CacheUtil.removeCompare(worksBean)
                    worksAdapter.removeAt(position)
                }
            }
        }
        mViewBind.flowContentLayout.setOnclickListener(object : ClickListener{
            override fun onClick(tag: String?) {
                tag?.let {
                    tag3 = it
                }
                if (tag.isNullOrEmpty()) {
                    fourth_recycler_view.gone()
                    line_view.gone()
                } else {
                    subTitleList?.let {
                        it.find { it.name == tag }.let {
                            it?.id?.let {
                                tag4 = ""
                                mViewModel.getDictionaryFourClassifyById(it)
                            }
                        }
                    }
                }
                requestData(true)
            }

            override fun onClickDown() {
                if(CacheUtil.getUserVipStatus() != 99){//超级会员
                    nav().navigateAction(
                        R.id.action_dictionaryListFragment_to_memberFragment,
                        bundleOf(MemberFragment.INTENT_KEY_TYPE to MemberFragment.INTENT_VALUE_DICTIONARY)
                    )
                }else{
                    mViewBind.flowContentLayout.updateDownStatus()
                }
            }
        })
        fourTagAdapter.setOnItemClickListener { adapter, view, position ->
            if (fourTagAdapter.selectedPosition == position) {
                fourTagAdapter.setClickSelectedPosition(0)
                tag4 = ""
            } else {
                fourTagAdapter.setClickSelectedPosition(position)
                tag4 = fourTagAdapter.data[position].name
                if(position == 0){
                    tag4 = ""
                }
            }
            requestData(true)
        }
        open_vip_btn.onClick {
            //开通超级会员
            nav().navigateAction(
                R.id.action_dictionaryListFragment_to_memberFragment,
                bundleOf(MemberFragment.INTENT_KEY_TYPE to MemberFragment.INTENT_VALUE_DICTIONARY)
            )
        }
        worksAdapter.setArgsType(args)
        searchKey = args.searchKey?:""
    }


    override fun onResume() {
        super.onResume()
        if (args.firstTag != Constant.COMPLEX_TYPE_NATIVE_COMPARE) {
            if (selectedPosition >= 0) {
                worksAdapter.updateCompareList()
                worksAdapter.notifyItemChanged(selectedPosition)
            }
        }
        if(worksAdapter.data.isNotEmpty()){
            worksAdapter.updateCompareList()
            worksAdapter.notifyDataSetChanged()
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
    fun requestData(isRefresh: Boolean = false) {
        if (args.firstTag == Constant.COMPLEX_TYPE_NATIVE_COMPARE) {
            var compareList = CacheUtil.getCompareList()
            ThreadUtils.getMainHandler().postDelayed({
                mViewBind.worksRefreshLayout?.isRefreshing = false
                mViewBind.worksRecyclerView.loadMoreFinish(false, false);
                worksAdapter.setList(compareList)
                if (compareList.size > 0) {
                    loadsir.showSuccess()
                } else {
                    loadsir.showEmpty()
                }
            }, 500)
        } else if (args.firstTag == Constant.COMPLEX_TYPE_COMPARE) {
            args.dictionarySetId?.let {
                mViewModel.findCompareItemPage(isRefresh, it)
            }
        } else {
            mViewModel.getDictionaryWorks(
                isRefresh,
                searchKey,
                args.firstTag ?: "",
                args.tag ?: "",
                tag3,
                tag4
            )
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.classifySubList.observe(viewLifecycleOwner) {
            it?.let { subList ->
                var list = mutableListOf<String>()
                subList.forEach {
                    list.add(it.name)
                }
                subTitleList = subList
                mViewBind.flowContentLayout.addViews(list)
            }
        }
        mViewModel.classifyFourSubList.observe(viewLifecycleOwner) {
            it?.let { subList ->
//                var list = mutableListOf<String>()
//                subList.forEach {
//                    list.add(it.name)
//                }

                if (subList.size > 0) {
                    fourth_recycler_view.visible()
                    line_view.visible()
                    fourTagAdapter.data = subList
                    fourTagAdapter.data.add(0, DictionaryMenuBean(-1,"4","全部", mutableListOf(),""))
                } else {
                    fourth_recycler_view.gone()
                    line_view.gone()
                }
                fourTagAdapter.data = subList
                fourTagAdapter.setClickSelectedPosition(0)
            } ?: let {
                fourth_recycler_view.gone()
                line_view.gone()
            }
        }
        mViewModel.worksDataState.observe(viewLifecycleOwner, Observer {
            //设值 新写了个拓展函数
            loadDictionaryListData(
                it,
                worksAdapter,
                loadsir,
                mViewBind.worksRecyclerView,
                mViewBind.worksRefreshLayout,
                Constant.COMPLEX_TYPE_DICTIONARY
            )
            if (it.isRefresh && it.listData.size > 8 && CacheUtil.getUserVipStatus() != 99) {
                open_vip_btn.visible()
            } else {
                open_vip_btn.gone()
            }
        })
        mViewModel.compareItemPageDataState.observe(viewLifecycleOwner, Observer {
            //图单对比列表
            compareItemPageData = it.listData
            loadDictionaryListData(
                it,
                worksAdapter,
                loadsir,
                mViewBind.worksRecyclerView,
                mViewBind.worksRefreshLayout,
                Constant.COMPLEX_TYPE_COMPARE
            )
        })

        mViewModel.deleteCompareItemsLiveData.observe(viewLifecycleOwner) {
            if (it) {
                ToastUtils.showShort("删除成功")
                intent_data_sub?.let {
                    deleteComparePageItem?.let { bean ->
                        Constant.compareItemPageData?.remove(bean)
                    }
                    worksAdapter.notifyDataSetChanged()
                }?: let {
                    deleteIdPosition?.let {
                        worksAdapter.removeAt(it)
                    }
                }
                EventBus.getDefault().post(CompareItemDeleteEvent())
            } else {
                ToastUtils.showShort("删除失败！")
            }
        }
        mViewModel.addCompareItemsLiveData.observe(viewLifecycleOwner) {
            //图单列表新增切图
            if(it){
                addComparePageItem?.let { bean ->
                    Constant.compareItemPageData?.add(bean)
                }
                ToastUtils.showShort("添加成功！")
                worksAdapter.notifyDataSetChanged()
                EventBus.getDefault().post(CompareItemAddEvent())
            }else{
                ToastUtils.showShort("添加失败！")
            }
        }
    }

    fun requestSearchData(searchKey: String) {
        this.searchKey = searchKey
        requestData(true)
    }


    override fun onDestroy() {
        super.onDestroy()
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