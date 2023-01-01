package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.app.util.StatusBarUtil
import com.gsq.iart.data.Constant.COMPLEX_TYPE_COLLECT
import com.gsq.iart.data.bean.ArgsType
import com.gsq.iart.databinding.FragmentMyCollectBinding
import com.gsq.iart.ui.fragment.home.WorksListFragment
import com.gsq.iart.viewmodel.WorksViewModel
import com.gsq.mvvm.ext.nav
import kotlinx.android.synthetic.main.fragment_my_collect.*

/**
 * 我的收藏
 */
class MyCollectFragment : BaseFragment<WorksViewModel, FragmentMyCollectBinding>() {

    private lateinit var worksListFragment: WorksListFragment

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
        title_layout.setBackListener {
            nav().navigateUp()
        }
        worksListFragment = WorksListFragment.start(ArgsType(COMPLEX_TYPE_COLLECT))
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.collect_frameLayout, worksListFragment)
        transaction.commit()
    }

    override fun createObserver() {
        super.createObserver()
    }
}