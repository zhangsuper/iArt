package com.gsq.iart.ui.fragment.search

import android.os.Bundle
import android.text.TextUtils
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.hideSoftKeyboard
import com.gsq.iart.app.util.MobAgentUtil
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.data.Constant
import com.gsq.iart.data.Constant.COMPLEX_TYPE_SEARCH
import com.gsq.iart.data.bean.ArgsType
import com.gsq.iart.data.bean.DictionaryArgsType
import com.gsq.iart.databinding.FragmentSearchBinding
import com.gsq.iart.ui.fragment.dictionary.DictionarySubListFragment
import com.gsq.iart.ui.fragment.home.WorksListFragment
import com.gsq.iart.viewmodel.SearchViewModel
import com.gsq.mvvm.ext.nav
import kotlinx.android.synthetic.main.fragment_search.*

/**
 * 搜索页
 */
class SearchFragment : BaseFragment<SearchViewModel, FragmentSearchBinding>() {

    //    private val searchResultFragment: WorksListFragment by lazy {
//        WorksListFragment.start(
//            ArgsType(
//                COMPLEX_TYPE_SEARCH
//            )
//        )
//    }
    private var searchResultFragment: WorksListFragment? = null
    private var dictionarySearchResultFragment: DictionarySubListFragment? = null
    private val searchInitFragment: SearchInitFragment by lazy { SearchInitFragment() }
    private var intentType: Int = 1

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
        intentType = arguments?.getInt(Constant.INTENT_DATA)?: 1
        searchInitFragment.setIntentType(intentType)
        back_btn.setOnClickListener {
            nav().navigateUp()
        }
        search_btn.setOnClickListener {
            var inputKey = search_input_view.text.toString()
            if (!TextUtils.isEmpty(inputKey)) {
                searchInitFragment.updateKey(inputKey)
                searchData(inputKey)
                KeyboardUtils.hideSoftInput(search_input_view)
                if(intentType == 2){
                    var eventMap = mutableMapOf<String, Any?>()
                    eventMap["query"] = inputKey
                    MobAgentUtil.onEvent("search_tudian", eventMap)
                }else {
                    var eventMap = mutableMapOf<String, Any?>()
                    eventMap["query"] = inputKey
                    MobAgentUtil.onEvent("search_guohua", eventMap)
                }
            } else {
                ToastUtils.showShort("请输入关键字")
            }
        }
        search_input_view.setOnEditorActionListener { textView, i, keyEvent ->
            var inputKey = search_input_view.text.toString()
            if (i == EditorInfo.IME_ACTION_SEARCH && !TextUtils.isEmpty(inputKey)) {
                searchInitFragment.updateKey(inputKey)
                searchData(inputKey)
                KeyboardUtils.hideSoftInput(search_input_view)
                if(intentType == 2){
                    var eventMap = mutableMapOf<String, Any?>()
                    eventMap["query"] = inputKey
                    MobAgentUtil.onEvent("search_tudian", eventMap)
                }else {
                    var eventMap = mutableMapOf<String, Any?>()
                    eventMap["query"] = inputKey
                    MobAgentUtil.onEvent("search_guohua", eventMap)
                }
                true
            }
            false
        }
        search_input_view.setOnClickListener {
//            if (!isInitFragment) {
//                transactionInitFragment()
//            }
        }
        transactionInitFragment()
        searchInitFragment.setOnClickItemListener {
            search_input_view.setText(it)
            searchData(it)
            KeyboardUtils.hideSoftInput(search_input_view)
        }
        search_input_view.requestFocus()
        ThreadUtils.getMainHandler().postDelayed({
            KeyboardUtils.showSoftInput()
        },200)
    }

    private fun transactionInitFragment() {
        transactionFragment(searchInitFragment)
        isInitFragment = true
    }

    private var isInitFragment = true
    private fun searchData(key: String) {
        hideSoftKeyboard(activity)
        var intentType = arguments?.getInt(Constant.INTENT_DATA)
        if(intentType == 1){
            if (!isInitFragment) {
                searchResultFragment?.let {
                    it.requestSearchData(key)
                }
                return
            }
            searchResultFragment =
                WorksListFragment.start(ArgsType(COMPLEX_TYPE_SEARCH, searchKey = key))
            searchResultFragment?.let {
                transactionFragment(it)
            }
            isInitFragment = false
        }else if(intentType == 2){
            if (!isInitFragment) {
                dictionarySearchResultFragment?.let {
                    it.requestSearchData(key)
                }
                return
            }
            dictionarySearchResultFragment =
                DictionarySubListFragment.start(DictionaryArgsType(searchKey = key))
            dictionarySearchResultFragment?.let {
                transactionFragment(it)
            }
            isInitFragment = false
        }
    }


    override fun createObserver() {
        super.createObserver()
//        mViewModel.itemClickKey.observe(viewLifecycleOwner, Observer {
//            searchData(it)
//        })
    }


    private fun transactionFragment(fragment: Fragment) {
        var transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.search_frameLayout, fragment)
        transaction.setReorderingAllowed(false)
        transaction.commit()
    }

    override fun onStop() {
        KeyboardUtils.hideSoftInput(search_input_view)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}