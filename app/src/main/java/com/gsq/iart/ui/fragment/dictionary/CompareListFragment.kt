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
import com.gsq.iart.data.event.CompareRenameEvent
import com.gsq.iart.databinding.FragmentCompareListBinding
import com.gsq.iart.ui.dialog.CompareSaveDialog
import com.gsq.iart.ui.dialog.DialogUtils
import com.gsq.iart.viewmodel.DictionaryViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.onClick
import com.gsq.mvvm.ext.view.visible
import kotlinx.android.synthetic.main.fragment_compare_list.add_btn
import kotlinx.android.synthetic.main.fragment_compare_list.clear_btn
import kotlinx.android.synthetic.main.fragment_compare_list.manage_btn
import kotlinx.android.synthetic.main.fragment_compare_list.save_btn
import kotlinx.android.synthetic.main.fragment_my_collect.title_layout
import org.greenrobot.eventbus.EventBus

class CompareListFragment : BaseFragment<DictionaryViewModel, FragmentCompareListBinding>() {

    private lateinit var listFragment: DictionarySubListFragment
    private var intentType: String? = null
    private var dictionarySetsBean: DictionarySetsBean? = null
    private var newName: String? = null

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
            add_btn.gone()
            clear_btn.text = "清空"
        } else if (intentType == COMPLEX_TYPE_COMPARE) {
            save_btn.gone()
            add_btn.visible()
            dictionarySetsBean =
                arguments?.getSerializable(Constant.INTENT_DATA) as? DictionarySetsBean
            dictionarySetsBean?.let {
                title_layout.setTitle(it.name)
            }
            title_layout.setCenterImage(R.drawable.icon_edit)
            clear_btn.text = "删除"
        }
        title_layout.setBackListener {
            nav().navigateUp()
        }
        title_layout.setCenterClickListener {
            //重命名
            CompareSaveDialog().setDialogType(2).setBackListener {
                dictionarySetsBean?.id?.let { id ->
                    newName = it
                    mViewModel.compareRename(id, it)
                }
            }.show(childFragmentManager)
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
                CompareSaveDialog().setDialogType(1).setBackListener {
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
            if (intentType == COMPLEX_TYPE_NATIVE_COMPARE) {
                //本地对比列表
                DialogUtils.showNormalDoubleButtonDialog(
                    activity,
                    "提示",
                    "是否清空对比列表？",
                    "确定",
                    "取消",
                    true
                ) { dialog, isLeft ->
                    if (!isLeft) {
                        CacheUtil.setCompareList(arrayListOf())
                        listFragment.lazyLoadData()
                    }
                }
            } else if (intentType == COMPLEX_TYPE_COMPARE) {
                //图单对比列表
                DialogUtils.showNormalDoubleButtonDialog(
                    activity,
                    "提示",
                    "是否删除整个图单？",
                    "确定",
                    "取消",
                    true
                ) { dialog, isLeft ->
                    if (!isLeft) {
                        dictionarySetsBean?.let {
                            mViewModel.deleteCompare(it.id)
                        }
                    }
                }
            }
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
        mViewModel.compareRenameLiveData.observe(viewLifecycleOwner) {
            if (it) {
                ToastUtils.showShort("修改成功！")
                newName?.let {
                    title_layout.setTitle(it)
                    dictionarySetsBean?.id?.let { id ->
                        EventBus.getDefault().post(CompareRenameEvent(id, it))
                    }

                }
            } else {
                ToastUtils.showShort("修改失败！")
            }
        }
        mViewModel.deleteCompareLiveData.observe(viewLifecycleOwner) {
            if (it) {
                nav().navigateUp()
                ToastUtils.showShort("删除成功")
            } else {
                ToastUtils.showShort("删除失败！")
            }
        }
    }
}