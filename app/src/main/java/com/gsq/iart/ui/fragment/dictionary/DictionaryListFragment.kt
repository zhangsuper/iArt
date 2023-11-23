package com.gsq.iart.ui.fragment.dictionary

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ThreadUtils
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.ext.bindViewPager2
import com.gsq.iart.app.ext.init
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.data.Constant
import com.gsq.iart.data.bean.ArgsType
import com.gsq.iart.data.bean.DictionaryArgsType
import com.gsq.iart.data.bean.DictionaryMenuBean
import com.gsq.iart.data.event.BigImageClickEvent
import com.gsq.iart.data.event.CompareEvent
import com.gsq.iart.databinding.FragmentDictionaryListBinding
import com.gsq.iart.ui.adapter.SearchHistoryAdapter
import com.gsq.iart.ui.fragment.home.WorksListFragment
import com.gsq.iart.viewmodel.DictionaryViewModel
import com.gsq.mvvm.ext.nav
import com.gsq.mvvm.ext.view.gone
import com.gsq.mvvm.ext.view.visible
import kotlinx.android.synthetic.main.fragment_dictionary_list.dictionary_magic_indicator
import kotlinx.android.synthetic.main.fragment_dictionary_list.dictionary_view_pager
import kotlinx.android.synthetic.main.fragment_dictionary_list.third_recycler_view
import kotlinx.android.synthetic.main.fragment_setting.title_layout
import kotlinx.android.synthetic.main.fragment_work_detail.content_view
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 图典列表
 */
class DictionaryListFragment : BaseFragment<DictionaryViewModel, FragmentDictionaryListBinding>()  {

    private lateinit var dictionaryMenuBean : DictionaryMenuBean
    private var position: Int = 0

    //标题集合
    var mDataList: ArrayList<String> = arrayListOf()

    //fragment集合
    var fragments: ArrayList<Fragment> = arrayListOf()

    private val mThreeDictionaryAdapter: SearchHistoryAdapter by lazy { SearchHistoryAdapter(arrayListOf()) }

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
        title_layout.setBackListener {
            nav().navigateUp()

        }
        title_layout.setRightClickListener {
            //对比列表
        }
        dictionaryMenuBean = arguments?.getSerializable(Constant.INTENT_DATA) as DictionaryMenuBean
        position = arguments?.getInt(Constant.INTENT_POSITION,0)?: 0
        title_layout.setTitle(dictionaryMenuBean.name)
        mDataList.add("全部")
        fragments.add(
            DictionarySubListFragment.start(
                DictionaryArgsType(firstTag = dictionaryMenuBean.name)
            )
        )
        dictionaryMenuBean.subs?.let {
            it.forEach {
                mDataList.add(it.name)
                fragments.add(
                    DictionarySubListFragment.start(
                        DictionaryArgsType(firstTag = dictionaryMenuBean.name, tag = it.name, pid = it.id)
                    )
                )
            }
        }

        //初始化viewpager2
        dictionary_view_pager.init(this, fragments)
        //初始化 magic_indicator
        dictionary_magic_indicator.bindViewPager2(dictionary_view_pager, mDataList)
        dictionary_view_pager.setCurrentItem(position, false)
        dictionary_view_pager.offscreenPageLimit = 1
        updateRightData()
        EventBus.getDefault().register(this)

    }

    private fun updateRightData(){
        var compareList = CacheUtil.getCompareList()
        if(compareList.size>0){
            title_layout.setRightText("对比列表（${compareList.size}）")
        }else{
            title_layout.setRightText("")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: CompareEvent?) {
        event?.let {
            updateRightData()
        }
    }



}