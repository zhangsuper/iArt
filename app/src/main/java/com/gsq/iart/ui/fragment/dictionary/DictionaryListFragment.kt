package com.gsq.iart.ui.fragment.dictionary

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.bindViewPager2
import com.gsq.iart.app.ext.init
import com.gsq.iart.data.Constant
import com.gsq.iart.data.bean.ArgsType
import com.gsq.iart.data.bean.DictionaryArgsType
import com.gsq.iart.data.bean.DictionaryMenuBean
import com.gsq.iart.databinding.FragmentDictionaryListBinding
import com.gsq.iart.ui.fragment.home.WorksListFragment
import com.gsq.iart.viewmodel.DictionaryViewModel
import com.gsq.mvvm.ext.nav
import kotlinx.android.synthetic.main.fragment_dictionary_list.dictionary_magic_indicator
import kotlinx.android.synthetic.main.fragment_dictionary_list.dictionary_view_pager
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
    }

}