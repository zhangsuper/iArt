package com.gsq.iart.viewmodel

import androidx.lifecycle.MutableLiveData
import com.gsq.iart.app.network.apiService
import com.gsq.iart.app.network.stateCallback.ListDataUiState
import com.gsq.iart.app.network.stateCallback.UpdateUiState
import com.gsq.iart.data.bean.PayConfigBean
import com.gsq.iart.data.bean.PayOrderBean
import com.gsq.iart.data.request.MemberPayRequestParam
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.request

class MemberViewModel : BaseViewModel() {

    var payConfigDataState: MutableLiveData<ListDataUiState<PayConfigBean>> = MutableLiveData()

    var preparePayDataState = MutableLiveData<UpdateUiState<PayOrderBean>>()

    /**
     * 支付套餐列表
     */
    fun getPayConfig() {
        request(
            { apiService.getPayConfig() },
            {
                //请求成功
                val listDataUiState =
                    ListDataUiState(
                        isSuccess = true,
                        isEmpty = it.isEmpty(),
                        listData = it
                    )
                payConfigDataState.value = listDataUiState
            },
            {
                //请求失败
                val listDataUiState =
                    ListDataUiState(
                        isSuccess = false,
                        errMessage = it.errorMsg,
                        listData = arrayListOf<PayConfigBean>()
                    )
                payConfigDataState.value = listDataUiState
            })
    }

    /**
     * 创建预支付订单
     */
    fun createPreparePay(id: Long) {
        var payRequestParam = MemberPayRequestParam(id)
        request(
            { apiService.createPreparePay(payRequestParam) },
            {
                //请求成功
                val updateUiState =
                    UpdateUiState(
                        isSuccess = true,
                        data = it
                    )
                preparePayDataState.value = updateUiState
            },
            {
                //请求失败
                val updateUiState =
                    UpdateUiState<PayOrderBean>(
                        isSuccess = false,
                        errorMsg = it.errorMsg,
                        data = null
                    )
                preparePayDataState.value = updateUiState
            }, true
        )
    }

}