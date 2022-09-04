package com.gsq.iart.viewmodel

import androidx.lifecycle.MutableLiveData
import com.gsq.iart.app.network.apiService
import com.gsq.iart.app.network.stateCallback.ListDataUiState
import com.gsq.iart.app.network.stateCallback.UpdateUiState
import com.gsq.iart.data.Constant.DEFAULT_REQUEST_SIZE
import com.gsq.iart.data.bean.ConditionClassifyBean
import com.gsq.iart.data.bean.WorksBean
import com.gsq.iart.data.request.WorkDetailRequestParam
import com.gsq.iart.data.request.WorkPageRequestParam
import com.gsq.iart.data.request.WorkPropSearchBean
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.request

/**
 * 作品viewmodel
 */
class WorksViewModel: BaseViewModel() {
    //页码
    var pageNo = 1

    //作品列表
    var worksDataState: MutableLiveData<ListDataUiState<WorksBean>> = MutableLiveData()

    //作品详情数据
    var workDetailDataState = MutableLiveData<UpdateUiState<WorksBean>>()

    //一级分类
    var conditionRootClassifys = MutableLiveData<ListDataUiState<ConditionClassifyBean>>()

    /**
     * 获取作品列表数据
     */
    fun getWorksListData(isRefresh: Boolean,
                         classifyId: Int,
                         orderType: Int = 0,
                         propSearchs: MutableList<WorkPropSearchBean>?,
                         searchKey: String = "") {
        if (isRefresh) {
            pageNo =  0
        }
        var workPageRequestParam = WorkPageRequestParam(
            classifyId,
            orderType,
            pageNo,
            DEFAULT_REQUEST_SIZE,
            propSearchs,
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

    /**
     * 获取作品详情
     */
    fun getWorkDetail(id: Int){
//        var requestParam = WorkDetailRequestParam(id)
        request(
            { apiService.getWorkDetail(id) },
            {
                //请求成功
                val updateDataUiState = UpdateUiState(
                    isSuccess = true,
                    data = it,
                )
                workDetailDataState.value = updateDataUiState
            },
            {
                //请求失败
                val updateDataUiState = UpdateUiState<WorksBean>(
                    isSuccess = false,
                    errorMsg = it.errorMsg
                )
                workDetailDataState.value = updateDataUiState
            })
    }

    /**
     * 获取所有分类
     */
    fun getConditionAllClassify(){
        request(
            { apiService.getConditionAllClassify()},
            {
                val listDataUiState =
                    ListDataUiState(
                        isSuccess = true,
                        isEmpty = it.isEmpty(),
                        listData = it
                    )
                conditionRootClassifys.value = listDataUiState
            },
            {
                //请求失败
                val listDataUiState =
                    ListDataUiState(
                        isSuccess = false,
                        errMessage = it.errorMsg,
                        listData = arrayListOf<ConditionClassifyBean>()
                    )
                conditionRootClassifys.value = listDataUiState
            }
        )
    }

    /**
     * 获取一级分类
     */
    fun getConditionRootClassify(){
        request(
            { apiService.getConditionRootClassify()},
            {
                val listDataUiState =
                    ListDataUiState(
                        isSuccess = true,
                        isEmpty = it.isEmpty(),
                        listData = it
                    )
                conditionRootClassifys.value = listDataUiState
            },
            {
                //请求失败
                val listDataUiState =
                    ListDataUiState(
                        isSuccess = false,
                        errMessage = it.errorMsg,
                        listData = arrayListOf<ConditionClassifyBean>()
                    )
                conditionRootClassifys.value = listDataUiState
            }
        )
    }

}