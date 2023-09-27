package com.gsq.iart.ui.fragment.dictionary

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.bindViewPager2
import com.gsq.iart.app.ext.init
import com.gsq.iart.data.Constant
import com.gsq.iart.data.bean.ArgsType
import com.gsq.iart.data.bean.DictionaryArgsType
import com.gsq.iart.data.bean.DictionaryMenuBean
import com.gsq.iart.databinding.FragmentDictionaryListBinding
import com.gsq.iart.ui.adapter.SearchHistoryAdapter
import com.gsq.iart.ui.fragment.home.WorksListFragment
import com.gsq.iart.viewmodel.DictionaryViewModel
import com.gsq.mvvm.ext.nav
import kotlinx.android.synthetic.main.fragment_dictionary_list.dictionary_magic_indicator
import kotlinx.android.synthetic.main.fragment_dictionary_list.dictionary_view_pager
import kotlinx.android.synthetic.main.fragment_dictionary_list.third_recycler_view
import kotlinx.android.synthetic.main.fragment_dictionary_sub_list.level_three_recycler_view
import kotlinx.android.synthetic.main.fragment_home.home_magic_indicator
import kotlinx.android.synthetic.main.fragment_home.home_view_pager
import kotlinx.android.synthetic.main.fragment_setting.title_layout

/**
 * 图典列表
 */
class DictionaryListFragment : BaseFragment<DictionaryViewModel, FragmentDictionaryListBinding>()  {

    private lateinit var dictionaryMenuBean : DictionaryMenuBean

    //标题集合
    var mDataList: ArrayList<String> = arrayListOf()

    //fragment集合
    var fragments: ArrayList<Fragment> = arrayListOf()

    private val mThreeDictionaryAdapter: SearchHistoryAdapter by lazy { SearchHistoryAdapter(arrayListOf()) }

    override fun initView(savedInstanceState: Bundle?) {
        title_layout.setBackListener {
            nav().navigateUp()

        }
        dictionaryMenuBean = arguments?.getSerializable(Constant.INTENT_DATA) as DictionaryMenuBean
        title_layout.setTitle(dictionaryMenuBean.name)
        mDataList.add("全部")
        fragments.add(
            DictionarySubListFragment.start(
                DictionaryArgsType("all")
            )
        )
        dictionaryMenuBean.subs.forEach {
            mDataList.add(it.name)
            fragments.add(
                DictionarySubListFragment.start(
                    DictionaryArgsType(it.name)
                )
            )
        }
        //初始化viewpager2
        dictionary_view_pager.init(this, fragments)
        //初始化 magic_indicator
        dictionary_magic_indicator.bindViewPager2(dictionary_view_pager, mDataList)

        //创建流式布局layout
        val layoutManager1 = FlexboxLayoutManager(context)
        //方向 主轴为水平方向，起点在左端
        layoutManager1.flexDirection = FlexDirection.ROW
        //左对齐
        layoutManager1.justifyContent = JustifyContent.FLEX_START
        //初始化搜搜历史Recyclerview
        third_recycler_view.init(layoutManager1, mThreeDictionaryAdapter, false)

        var list = mutableListOf<String>()
        list.add("汉")
        list.add("宋")
        list.add("秦")
        list.add("元")
        list.add("明")
        list.add("清")
        mThreeDictionaryAdapter.data = list
    }

}