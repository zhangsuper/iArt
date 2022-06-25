package com.gsq.iart.app.ext

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

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

//fun ViewPager2.initMain(fragment: Fragment): ViewPager2 {
//    //是否可滑动
//    this.isUserInputEnabled = false
//    this.offscreenPageLimit = 5
//    //设置适配器
//    adapter = object : FragmentStateAdapter(fragment) {
//        override fun createFragment(position: Int): Fragment {
//            when (position) {
//                0 -> {
//                    return HomeFragment()
//                }
//                1 -> {
//                    return ProjectFragment()
//                }
//                2 -> {
//                    return TreeArrFragment()
//                }
//                3 -> {
//                    return PublicNumberFragment()
//                }
//                4 -> {
//                    return MeFragment()
//                }
//                else -> {
//                    return HomeFragment()
//                }
//            }
//        }
//        override fun getItemCount() = 5
//    }
//    return this
//}

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
