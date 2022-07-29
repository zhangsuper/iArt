package com.gsq.iart.ui.fragment.mine

import android.os.Bundle
import com.gsq.iart.R
import com.gsq.iart.app.base.BaseFragment
import com.gsq.iart.data.bean.VipPriceBean
import com.gsq.iart.databinding.FragmentMemberBinding
import com.gsq.iart.ui.adapter.VipPriceAdapter
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.nav
import kotlinx.android.synthetic.main.fragment_member.*

/**
 * 我的会员
 */
class MemberFragment: BaseFragment<BaseViewModel,FragmentMemberBinding>() {

    private val vipPriceAdapter: VipPriceAdapter by lazy { VipPriceAdapter() }
    private var priceList: MutableList<VipPriceBean> = mutableListOf()
    private var payType = 1 //1:微信支付  2：支付宝支付

    override fun initView(savedInstanceState: Bundle?) {
        title_layout.setBackListener {
            nav().navigateUp()

        }
        price_recycler_view.adapter = vipPriceAdapter
        initDate()
        vipPriceAdapter.setOnItemClickListener { adapter, view, position ->
            vipPriceAdapter.selectItem(adapter.getItem(position) as VipPriceBean)
        }
        wechat_pay_view.setOnClickListener {
            payType = 1
            iv_wechat_pay_selected.setImageResource(R.drawable.check_box_selected)
            iv_ali_pay_selected.setImageResource(R.drawable.check_box_unselected)
        }
        ali_pay_view.setOnClickListener {
            payType = 2
            iv_wechat_pay_selected.setImageResource(R.drawable.check_box_unselected)
            iv_ali_pay_selected.setImageResource(R.drawable.check_box_selected)
        }
    }

    private fun initDate(){
        var bean1 = VipPriceBean(36,399)
        var bean2 = VipPriceBean(12,169)
        var bean3 = VipPriceBean(6,99)
        var bean4 = VipPriceBean(3,69)
        var bean5 = VipPriceBean(1,29)
        priceList.add(bean5)
        priceList.add(bean4)
        priceList.add(bean3)
        priceList.add(bean2)
        priceList.add(bean1)
        vipPriceAdapter.data = priceList
    }

}