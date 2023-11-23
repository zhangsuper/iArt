package com.gsq.iart.ui.fragment.dictionary

import android.os.Bundle
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.data.Constant.COMPLEX_TYPE_COMPARE
import com.gsq.iart.data.bean.DictionaryArgsType
import com.gsq.iart.databinding.FragmentCompareListBinding
import com.gsq.iart.viewmodel.DictionaryViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.view.onClick
import kotlinx.android.synthetic.main.fragment_compare_list.clear_btn
import kotlinx.android.synthetic.main.fragment_compare_list.manage_btn
import kotlinx.android.synthetic.main.fragment_compare_list.save_btn
import kotlinx.android.synthetic.main.fragment_my_collect.title_layout

class CompareListFragment: BaseFragment<DictionaryViewModel, FragmentCompareListBinding>() {

    private lateinit var listFragment: DictionarySubListFragment

    override fun onResume() {
        super.onResume()
        StatusBarUtil.init(
            requireActivity(),
            fitSystem = true,
            statusBarColor = R.color.white,
            isDarkFont = true
        )
    }

    override fun initView(savedInstanceState: Bundle?) {
        title_layout.setTitle("对比列表")
        title_layout.setBackListener {
            nav().navigateUp()
        }
        listFragment = DictionarySubListFragment.start(DictionaryArgsType(firstTag = COMPLEX_TYPE_COMPARE))
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.compare_frameLayout, listFragment)
        transaction.commit()

        manage_btn.onClick {
            //管理
        }
        save_btn.onClick {
            //保存
        }
        clear_btn.onClick {
            //清空
            CacheUtil.setCompareList(arrayListOf())
            listFragment.lazyLoadData()
        }
    }
}