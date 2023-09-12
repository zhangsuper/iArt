package com.gsq.iart.viewmodel

import androidx.lifecycle.MutableLiveData
import com.gsq.iart.app.network.apiService
import com.gsq.iart.app.network.stateCallback.ListDataUiState
import com.gsq.iart.data.bean.HomeClassifyBean
import com.gsq.iart.data.bean.WorksBean
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.request

class DictionaryViewModel: BaseViewModel() {

    var classifyList: MutableLiveData<ArrayList<HomeClassifyBean>> = MutableLiveData()

    /**
     * 请求图典分类列表
     */
    fun getDictionaryClassifyList(){
        request(
            { apiService.getClassifyList()},
            {
                //请求成功
                classifyList.value = it
            },
            {
                //请求失败
                classifyList.value = null
            })
    }
}