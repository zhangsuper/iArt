package com.gsq.iart.ui.fragment.dictionary

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ThreadUtils
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.bindViewPager2
import com.gsq.iart.app.ext.init
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.data.Constant
import com.gsq.iart.data.bean.DictionaryArgsType
import com.gsq.iart.data.bean.DictionaryMenuBean
import com.gsq.iart.data.bean.DictionarySetsBean
import com.gsq.iart.data.event.CompareEvent
import com.gsq.iart.data.event.CompareItemAddEvent
import com.gsq.iart.databinding.FragmentDictionaryListBinding
import com.gsq.iart.ui.adapter.SearchHistoryAdapter
import com.gsq.iart.viewmodel.DictionaryViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.navigateAction
import kotlinx.android.synthetic.main.fragment_dictionary_list.dictionary_magic_indicator
import kotlinx.android.synthetic.main.fragment_dictionary_list.dictionary_view_pager
import kotlinx.android.synthetic.main.fragment_setting.title_layout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 图典列表
 */
class DictionaryListFragment : BaseFragment<DictionaryViewModel, FragmentDictionaryListBinding>() {

    private lateinit var dictionaryMenuBean: DictionaryMenuBean
    private var position: Int = 0

    //标题集合
    var mDataList: ArrayList<String> = arrayListOf()

    //fragment集合
    var fragments: ArrayList<Fragment> = arrayListOf()

    private var intent_data_sub: DictionarySetsBean? = null

    private val mThreeDictionaryAdapter: SearchHistoryAdapter by lazy {
        SearchHistoryAdapter(
            arrayListOf()
        )
    }

    override fun onResume() {
        super.onResume()
        ThreadUtils.getMainHandler().postDelayed({
            StatusBarUtil.init(
                requireActivity(),
                fitSystem = true,
                statusBarColor = R.color.white,
                isDarkFont = true
            )
        }, 100)
    }

    override fun initView(savedInstanceState: Bundle?) {
        intent_data_sub = arguments?.getSerializable(Constant.INTENT_DATA_SUB) as? DictionarySetsBean
        title_layout.setBackListener {
            nav().navigateUp()

        }
        title_layout.setRightClickListener {
            //对比列表
            intent_data_sub?.let {
                var bundle = Bundle()
                bundle.putString(Constant.INTENT_TYPE, Constant.COMPLEX_TYPE_COMPARE)
                bundle.putSerializable(Constant.INTENT_DATA, it)
                nav().navigateAction(
                    R.id.action_dictionaryListFragment_to_compareListFragment,
                    bundle
                )
            }?: let {
                var bundle = Bundle()
                bundle.putString(Constant.INTENT_TYPE, Constant.COMPLEX_TYPE_NATIVE_COMPARE)
                nav().navigateAction(R.id.action_dictionaryListFragment_to_compareListFragment, bundle)
            }
        }
        dictionaryMenuBean = arguments?.getSerializable(Constant.INTENT_DATA) as DictionaryMenuBean
        position = arguments?.getInt(Constant.INTENT_POSITION, 0) ?: 0
        title_layout.setTitle(dictionaryMenuBean.name)
        mDataList.add("全部")
        fragments.add(
            DictionarySubListFragment.start(
                DictionaryArgsType(firstTag = dictionaryMenuBean.name, dictionarySetsBean = intent_data_sub)
            )
        )
        dictionaryMenuBean.subs?.let {
            it.forEach {
                mDataList.add(it.name)
                fragments.add(
                    DictionarySubListFragment.start(
                        DictionaryArgsType(
                            firstTag = dictionaryMenuBean.name,
                            tag = it.name,
                            pid = it.id,
                            dictionarySetsBean = intent_data_sub
                        )
                    )
                )
            }
        }

        //初始化viewpager2
        dictionary_view_pager.init(this, fragments)
        //初始化 magic_indicator
        dictionary_magic_indicator.bindViewPager2(dictionary_view_pager, mDataList)
        dictionary_view_pager.setCurrentItem(position, false)
        dictionary_view_pager.offscreenPageLimit = 20
        updateRightData()
        EventBus.getDefault().register(this)

    }

    private fun updateRightData() {
        intent_data_sub?.let {
            title_layout.setRightText("${it.name} (${it.num})")
        }?: let {
            var compareList = CacheUtil.getCompareList()
            if (compareList.size > 0) {
                title_layout.setRightText("对比列表（${compareList.size}）")
            } else {
                title_layout.setRightText("")
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: CompareEvent?) {//本地对比列表新增
        event?.let {
            updateRightData()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: CompareItemAddEvent?) {//图单列表新增
        event?.let {
            intent_data_sub?.let {
                title_layout.setRightText("${it.name} (${Constant.compareItemPageData?.size})")
            }
        }
    }


}