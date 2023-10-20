package com.gsq.iart.ui.fragment.dictionary

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ThreadUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.bindViewPager2
import com.gsq.iart.app.ext.init
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.data.Constant
import com.gsq.iart.data.bean.WorksBean
import com.gsq.iart.databinding.FragmentDictionaryBinding
import com.gsq.iart.ui.adapter.AllConditionAdapter
import com.gsq.iart.ui.adapter.DictionaryMenuAdapter
import com.gsq.iart.viewmodel.DictionaryViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import kotlinx.android.synthetic.main.fragment_dictionary.recycler_view
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * 图典
 */
class DictionaryFragment : BaseFragment<DictionaryViewModel, FragmentDictionaryBinding>() {

    private var mAdapter: DictionaryMenuAdapter? = null

    override fun onResume() {
        super.onResume()
        ThreadUtils.getMainHandler().postDelayed({
            StatusBarUtil.init(
                requireActivity(),
                fitSystem = true,
                statusBarColor = R.color.white,
                isDarkFont = true
            )
        },100)

    }

    override fun initView(savedInstanceState: Bundle?) {
        home_search_view.setOnClickListener {
            //跳转搜索节面
            nav().navigateAction(R.id.action_mainFragment_to_searchFragment)
        }
        mAdapter = DictionaryMenuAdapter()
        mAdapter!!.setClickBackListener { bean, position ->
            var bundle = Bundle()
            bundle.putSerializable(Constant.INTENT_DATA, bean)
            bundle.putInt(Constant.INTENT_POSITION, position)
            nav().navigateAction(R.id.action_mainFragment_to_dictionaryListFragment, bundle)
        }
        recycler_view.adapter = mAdapter
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        mViewModel.getDictionaryClassifyList()//请求图典菜单列表
    }


    override fun createObserver() {
        super.createObserver()
        mViewModel.classifyList.observe(viewLifecycleOwner){
            if(it!=null){
                mAdapter?.data = it
                mAdapter?.notifyDataSetChanged()
            }
        }
    }

}