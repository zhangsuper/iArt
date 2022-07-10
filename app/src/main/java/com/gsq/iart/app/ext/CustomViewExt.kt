package com.gsq.iart.app.ext

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.chad.library.adapter.base.BaseQuickAdapter
import com.gsq.iart.R
import com.gsq.iart.app.network.stateCallback.ListDataUiState
import com.gsq.iart.app.util.SettingUtil
import com.gsq.iart.app.weight.loadCallBack.EmptyCallback
import com.gsq.iart.app.weight.loadCallBack.ErrorCallback
import com.gsq.iart.app.weight.loadCallBack.LoadingCallback
import com.gsq.iart.app.weight.recyclerview.DefineLoadMoreView
import com.gsq.iart.ui.fragment.home.HomeFragment
import com.gsq.iart.ui.fragment.mine.MineFragment
import com.gsq.mvvm.base.appContext
import com.gsq.mvvm.ext.util.toHtml
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import com.yh.bottomnavigation_base.IMenuListener
import com.yh.bottomnavigationex.BottomNavigationViewEx
import kotlinx.android.synthetic.main.fragment_main.view.*
import me.hgj.jetpackmvvm.demo.app.weight.viewpager.ScaleTransitionPagerTitleView
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

fun LoadService<*>.setErrorText(message: String) {
    if (message.isNotEmpty()) {
        this.setCallBack(ErrorCallback::class.java) { _, view ->
            view.findViewById<TextView>(R.id.error_text).text = message
        }
    }
}

/**
 * 设置错误布局
 * @param message 错误布局显示的提示内容
 */
fun LoadService<*>.showError(message: String = "") {
    this.setErrorText(message)
    this.showCallback(ErrorCallback::class.java)
}

/**
 * 设置空布局
 */
fun LoadService<*>.showEmpty() {
    this.showCallback(EmptyCallback::class.java)
}

/**
 * 设置加载中
 */
fun LoadService<*>.showLoading() {
    this.showCallback(LoadingCallback::class.java)
}

fun loadServiceInit(view: View, callback: () -> Unit): LoadService<Any> {
    val loadsir = LoadSir.getDefault().register(view) {
        //点击重试时触发的操作
        callback.invoke()
    }
    loadsir.showSuccess()
    SettingUtil.setLoadingColor(SettingUtil.getColor(appContext), loadsir)
    return loadsir
}

/**
 * 描述　:项目中自定义类的拓展函数
 */
//绑定普通的Recyclerview
fun RecyclerView.init(
    layoutManger: RecyclerView.LayoutManager,
    bindAdapter: RecyclerView.Adapter<*>,
    isScroll: Boolean = true
): RecyclerView {
    layoutManager = layoutManger
    setHasFixedSize(true)
    adapter = bindAdapter
    isNestedScrollingEnabled = isScroll
    return this
}

fun SwipeRecyclerView.initFooter(loadmoreListener: SwipeRecyclerView.LoadMoreListener): DefineLoadMoreView {
    val footerView = DefineLoadMoreView(appContext)
    //给尾部设置颜色
    footerView.setLoadViewColor(SettingUtil.getOneColorStateList(appContext))
    //设置尾部点击回调
    footerView.setmLoadMoreListener(SwipeRecyclerView.LoadMoreListener {
        footerView.onLoading()
        loadmoreListener.onLoadMore()
    })
    this.run {
        //添加加载更多尾部
        addFooterView(footerView)
        setLoadMoreView(footerView)
        //设置加载更多回调
        setLoadMoreListener(loadmoreListener)
    }
    return footerView
}

//设置适配器的列表动画
fun BaseQuickAdapter<*, *>.setAdapterAnimation(mode: Int) {
    //等于0，关闭列表动画 否则开启
    if (mode == 0) {
        this.animationEnable = false
    } else {
        this.animationEnable = true
        this.setAnimationWithDefault(BaseQuickAdapter.AnimationType.values()[mode - 1])
    }
}

fun MagicIndicator.bindViewPager2(
    viewPager: ViewPager2,
    mStringList: List<String> = arrayListOf(),
    action: (index: Int) -> Unit = {}) {
    val commonNavigator = CommonNavigator(appContext)
    commonNavigator.adapter = object : CommonNavigatorAdapter() {

        override fun getCount(): Int {
            return  mStringList.size
        }
        override fun getTitleView(context: Context, index: Int): IPagerTitleView {
            return ScaleTransitionPagerTitleView(appContext).apply {
                //设置文本
                text = mStringList[index].toHtml()
                //字体大小
                textSize = 18f
                //未选中颜色
                normalColor = appContext.resources.getColor(R.color.color_888888)
                //选中颜色
                selectedColor = appContext.resources.getColor(R.color.color_141414)
                //点击事件
                setOnClickListener {
                    viewPager.currentItem = index
                    action.invoke(index)
                }
            }
        }
        override fun getIndicator(context: Context): IPagerIndicator {
            return LinePagerIndicator(context).apply {
                mode = LinePagerIndicator.MODE_EXACTLY
                //线条的宽高度
                lineHeight = UIUtil.dip2px(appContext, 3.0).toFloat()
                lineWidth = UIUtil.dip2px(appContext, 30.0).toFloat()
                //线条的圆角
                roundRadius = UIUtil.dip2px(appContext, 6.0).toFloat()
                startInterpolator = AccelerateInterpolator()
                endInterpolator = DecelerateInterpolator(2.0f)
                //线条的颜色
                setColors(Color.WHITE)
            }
        }
    }
    this.navigator = commonNavigator

    viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            this@bindViewPager2.onPageSelected(position)
            action.invoke(position)
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            this@bindViewPager2.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            this@bindViewPager2.onPageScrollStateChanged(state)
        }
    })
}

fun ViewPager2.init(
    fragment: Fragment,
    fragments: ArrayList<Fragment>,
    isUserInputEnabled: Boolean = true
): ViewPager2 {
    //是否可滑动
    this.isUserInputEnabled = isUserInputEnabled
    //设置适配器
    adapter = object : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int) = fragments[position]
        override fun getItemCount() = fragments.size
    }
    return this
}

fun ViewPager2.initMain(fragment: Fragment): ViewPager2 {
    //是否可滑动
    this.isUserInputEnabled = false
    this.offscreenPageLimit = 5
    //设置适配器
    adapter = object : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> {
                    return HomeFragment()
                }
                1 -> {
                    return MineFragment()
                }
                else -> {
                    return HomeFragment()
                }
            }
        }
        override fun getItemCount() = 2
    }
    return this
}

//初始化 SwipeRefreshLayout
fun SwipeRefreshLayout.init(onRefreshListener: () -> Unit) {
    this.run {
        setOnRefreshListener {
            onRefreshListener.invoke()
        }
        //设置主题颜色
        setColorSchemeColors(SettingUtil.getColor(appContext))
    }
}

fun BottomNavigationViewEx.init(navigationItemSelectedAction: (Int) -> Unit): BottomNavigationViewEx {
    enableAnimation(true)
//    enableShiftingMode(false)
//    enableItemShiftingMode(true)
//    itemIconTintList = SettingUtil.getColorStateList(SettingUtil.getColor(appContext))
//    itemTextColor = SettingUtil.getColorStateList(appContext)
    setTextSize(12F)
    setIconSize(30F)
    setMenuListener(object: IMenuListener{
        override fun onNavigationItemSelected(
            position: Int,
            menu: MenuItem,
            isReSelected: Boolean
        ): Boolean {
            navigationItemSelectedAction.invoke(menu.itemId)
            return true
        }
    })
    return this
}


/**
 * 拦截BottomNavigation长按事件 防止长按时出现Toast
 * @receiver BottomNavigationViewEx
 * @param ids IntArray
 */
fun BottomNavigationViewEx.interceptLongClick(vararg ids:Int) {
    val bottomNavigationMenuView: ViewGroup = this.mainBottom.getBNMenuView()
    for (index in ids.indices){
        bottomNavigationMenuView.getChildAt(index).findViewById<View>(ids[index]).setOnLongClickListener {
            true
        }
    }
}

/**
 * 隐藏软键盘
 */
fun hideSoftKeyboard(activity: Activity?) {
    activity?.let { act ->
        val view = act.currentFocus
        view?.let {
            val inputMethodManager =
                act.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}

/**
 * 加载列表数据
 */
fun <T> loadListData(
    data: ListDataUiState<T>,
    baseQuickAdapter: BaseQuickAdapter<T, *>,
    loadService: LoadService<*>,
    recyclerView: SwipeRecyclerView,
    swipeRefreshLayout: SwipeRefreshLayout?
) {
    swipeRefreshLayout?.isRefreshing = false
    recyclerView.loadMoreFinish(data.isEmpty, data.hasMore)
    if (data.isSuccess) {
        //成功
        when {
            //第一页并没有数据 显示空布局界面
            data.isFirstEmpty -> {
                loadService.showEmpty()
            }
            //是第一页
            data.isRefresh -> {
                baseQuickAdapter.setList(data.listData)
                loadService.showSuccess()
            }
            //不是第一页
            else -> {
                baseQuickAdapter.addData(data.listData)
                loadService.showSuccess()
            }
        }
    } else {
        //失败
        if (data.isRefresh) {
            //如果是第一页，则显示错误界面，并提示错误信息
            loadService.showError(data.errMessage)
        } else {
            recyclerView.loadMoreError(0, data.errMessage)
        }
    }
}
