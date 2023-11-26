package com.gsq.iart.ui.fragment.dictionary

import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.data.Constant
import com.gsq.iart.data.Constant.COMPLEX_TYPE_COMPARE
import com.gsq.iart.data.Constant.COMPLEX_TYPE_NATIVE_COMPARE
import com.gsq.iart.data.bean.DictionaryArgsType
import com.gsq.iart.data.bean.DictionarySetsBean
import com.gsq.iart.databinding.FragmentCompareListBinding
import com.gsq.iart.ui.dialog.CompareSaveDialog
import com.gsq.iart.viewmodel.DictionaryViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.onClick
import com.gsq.mvvm.ext.view.visible
import kotlinx.android.synthetic.main.fragment_compare_list.clear_btn
import kotlinx.android.synthetic.main.fragment_compare_list.manage_btn
import kotlinx.android.synthetic.main.fragment_compare_list.save_btn
import kotlinx.android.synthetic.main.fragment_my_collect.title_layout

class CompareListFragment : BaseFragment<DictionaryViewModel, FragmentCompareListBinding>() {

    private lateinit var listFragment: DictionarySubListFragment
    private var intentType: String? = null
    private var dictionarySetsBean: DictionarySetsBean? = null

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
        intentType = arguments?.getString(Constant.INTENT_TYPE, COMPLEX_TYPE_NATIVE_COMPARE)
        if (intentType == COMPLEX_TYPE_NATIVE_COMPARE) {
            title_layout.setTitle("对比列表")
            save_btn.visible()
        } else if (intentType == COMPLEX_TYPE_COMPARE) {
            save_btn.gone()
            dictionarySetsBean =
                arguments?.getSerializable(Constant.INTENT_DATA) as? DictionarySetsBean
            dictionarySetsBean?.let {
                title_layout.setTitle(it.name)
            }
        }
        title_layout.setBackListener {
            nav().navigateUp()
        }
        listFragment =
            DictionarySubListFragment.start(
                DictionaryArgsType(
                    firstTag = intentType,
                    dictionarySetId = dictionarySetsBean?.id
                )
            )
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.compare_frameLayout, listFragment)
        transaction.commit()

        manage_btn.onClick {
            //管理
        }
        save_btn.onClick {
            //保存
            if (CacheUtil.isLogin()) {
                CompareSaveDialog().setBackListener {
                    var compareList = CacheUtil.getCompareList()
                    var compareIds = mutableListOf<Int>()
                    compareList.forEach {
                        compareIds.add(it.id)
                    }
                    mViewModel.addCompare(it, compareIds)
                }.show(childFragmentManager)
            } else {
                //跳转登录界面
                nav().navigateAction(R.id.action_compareListFragment_to_loginFragment)
            }
        }
        clear_btn.onClick {
            //清空
            CacheUtil.setCompareList(arrayListOf())
            listFragment.lazyLoadData()
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.addComparePageLiveData.observe(viewLifecycleOwner) {
            if (it) {
                //添加图单成功 退出，清空
                CacheUtil.setCompareList(arrayListOf())
                ToastUtils.showShort("图单已保存！")
                nav().navigateUp()
            } else {
                //添加图单失败
                ToastUtils.showShort("图单保存失败！")
            }
        }
    }
}