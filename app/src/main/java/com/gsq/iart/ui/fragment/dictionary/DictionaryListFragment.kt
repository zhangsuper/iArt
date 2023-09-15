package com.gsq.iart.ui.fragment.dictionary

import android.os.Bundle
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.data.Constant
import com.gsq.iart.data.bean.DictionaryMenuBean
import com.gsq.iart.databinding.FragmentDictionaryListBinding
import com.gsq.iart.viewmodel.DictionaryViewModel
import com.gsq.mvvm.ext.nav
import kotlinx.android.synthetic.main.fragment_setting.title_layout

/**
 * 图典列表
 */
class DictionaryListFragment : BaseFragment<DictionaryViewModel, FragmentDictionaryListBinding>()  {

    private lateinit var dictionaryMenuBean : DictionaryMenuBean

    override fun initView(savedInstanceState: Bundle?) {
        title_layout.setBackListener {
            nav().navigateUp()

        }
        dictionaryMenuBean = arguments?.getSerializable(Constant.INTENT_DATA) as DictionaryMenuBean
        title_layout.setTitle(dictionaryMenuBean.name)
    }

}