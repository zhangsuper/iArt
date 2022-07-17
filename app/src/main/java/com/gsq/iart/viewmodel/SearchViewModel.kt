package com.gsq.iart.viewmodel

import androidx.lifecycle.MutableLiveData
import com.gsq.iart.app.network.apiService
import com.gsq.iart.app.network.stateCallback.ListDataUiState
import com.gsq.iart.app.util.CacheUtil
import com.gsq.iart.data.bean.SearchResponse
import com.gsq.iart.data.bean.WorksBean
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.launch
import com.gsq.mvvm.ext.request
import com.gsq.mvvm.state.ResultState

class SearchViewModel: BaseViewModel() {

    var searchHistoryList: MutableLiveData<ArrayList<String>> = MutableLiveData()
    var searchHotList: MutableLiveData<ResultState<ArrayList<SearchResponse>>> = MutableLiveData()

    var itemClickKey: MutableLiveData<String> = MutableLiveData()

    /**
     * 获取热门数据
     */
    fun getHotData() {
        request({ apiService.getSearchHotKey() }, searchHotList)
    }

    /**
     * 获取历史数据
     */
    fun getHistoryData() {
        launch({
            CacheUtil.getSearchHistoryData()
        }, {
            searchHistoryList.value = it
        }, {
            //获取本地历史数据出异常了
        })
    }

}