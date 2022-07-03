package com.gsq.iart.viewmodel

import androidx.lifecycle.MutableLiveData
import com.gsq.iart.app.network.apiService
import com.gsq.iart.app.network.stateCallback.ListDataUiState
import com.gsq.iart.data.bean.WorksBean
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
    fun getWorksListData(isRefresh: Boolean,cid: Int) {
        if (isRefresh) {
            pageNo =  1
        }
        request({ apiService.getWorksDataByType(pageNo, cid) }, {
            //请求成功
            pageNo++
            val listDataUiState =
                ListDataUiState(
                    isSuccess = true,
                    isRefresh = isRefresh,
                    isEmpty = it.isEmpty(),
                    hasMore = it.hasMore(),
                    isFirstEmpty = isRefresh && it.isEmpty(),
                    listData = it.datas
                )
            worksDataState.value = listDataUiState
        }, {
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