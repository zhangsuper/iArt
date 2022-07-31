package com.gsq.iart.viewmodel

import androidx.lifecycle.MutableLiveData
import com.gsq.iart.app.network.apiService
import com.gsq.iart.app.network.stateCallback.ListDataUiState
import com.gsq.iart.data.Constant.DEFAULT_REQUEST_SIZE
import com.gsq.iart.data.bean.WorksBean
import com.gsq.iart.data.request.WorkPageRequestParam
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.request

/**
 * 作品viewmodel
 */
class WorksViewModel: BaseViewModel() {
    //页码
    var pageNo = 1

    var worksDataState: MutableLiveData<ListDataUiState<WorksBean>> = MutableLiveData()

    /**
     * 获取作品列表数据
     */
    fun getWorksListData(isRefresh: Boolean,
                         classifyId: Int,
                         orderType: Int = 0,
                         propSearchFiled: String = "",
                         propSearchValue: String = "",
                         searchKey: String = "") {
        if (isRefresh) {
            pageNo =  0
        }
        var workPageRequestParam = WorkPageRequestParam(
            classifyId,
            orderType,
            pageNo,
            DEFAULT_REQUEST_SIZE,
            propSearchFiled,
            propSearchValue,
            searchKey)
        request(
            { apiService.getWorksDataByType(workPageRequestParam) },
            {
            //请求成功
            pageNo++
            val listDataUiState =
                ListDataUiState(
                    isSuccess = true,
                    isRefresh = isRefresh,
                    isEmpty = it.isEmpty(),
//                    hasMore = it.hasMore(),
                    isFirstEmpty = isRefresh && it.isEmpty(),
                    listData = it
                )
            worksDataState.value = listDataUiState
        },
            {
            //请求失败
            val listDataUiState =
                ListDataUiState(
                    isSuccess = false,
                    errMessage = it.errorMsg,
                    isRefresh = isRefresh,
                    listData = arrayListOf<WorksBean>()
                )
            worksDataState.value = listDataUiState
        })
    }

}