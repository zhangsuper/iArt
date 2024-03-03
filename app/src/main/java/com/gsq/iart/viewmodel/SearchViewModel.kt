package com.gsq.iart.viewmodel

import androidx.lifecycle.MutableLiveData
import com.gsq.iart.app.network.apiService
import com.gsq.iart.app.network.stateCallback.ListDataUiState
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.data.bean.SearchResponse
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.launch
import com.gsq.mvvm.ext.request
import com.gsq.mvvm.state.ResultState

class SearchViewModel : BaseViewModel() {

    var searchHistoryList: MutableLiveData<ArrayList<String>> = MutableLiveData()
    var searchHotList: MutableLiveData<ResultState<ArrayList<SearchResponse>>> = MutableLiveData()

    var itemClickKey: MutableLiveData<String> = MutableLiveData()

    var hotSearchDataState = MutableLiveData<ListDataUiState<String>>()

    /**
     * 获取热门数据
     */
    fun getHotData(intentType: Int) {
        request(
            { apiService.getHotSearch(intentType) },
            {
                //请求成功
                val updateDataUiState = ListDataUiState(
                    isSuccess = true,
                    isEmpty = it.isEmpty(),
                    listData = it
                )
                hotSearchDataState.value = updateDataUiState
            },
            {
                //请求失败
                val updateDataUiState = ListDataUiState<String>(
                    isSuccess = false,
                    errMessage = it.errorMsg,
                    listData = arrayListOf()
                )
                hotSearchDataState.value = updateDataUiState
            })
    }

    /**
     * 获取历史数据
     */
    fun getHistoryData(intentType: Int) {
        launch({
            if(intentType == 2){
                CacheUtil.getDictionarySearchHistoryData()
            }else{
                CacheUtil.getSearchHistoryData()
            }

        }, {
            searchHistoryList.value = it
        }, {
            //获取本地历史数据出异常了
        })
    }

    fun getDictionaryHistoryData() {
        launch({
            CacheUtil.getDictionarySearchHistoryData()
        }, {
            searchHistoryList.value = it
        }, {
            //获取本地历史数据出异常了
        })
    }

}