package com.gsq.iart.viewmodel

import androidx.lifecycle.MutableLiveData
import com.gsq.iart.app.network.apiService
import com.gsq.iart.app.network.stateCallback.ListDataUiState
import com.gsq.iart.data.bean.DictionaryMenuBean
import com.gsq.iart.data.bean.HomeClassifyBean
import com.gsq.iart.data.bean.WorksBean
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.request

class DictionaryViewModel: BaseViewModel() {

    var classifyList: MutableLiveData<ArrayList<DictionaryMenuBean>> = MutableLiveData()

    /**
     * 请求图典分类列表
     */
    fun getDictionaryClassifyList(){
        request(
            { apiService.getDictionaryClassify()},
            {
                //请求成功
                classifyList.value = it
            },
            {
                //请求失败
                classifyList.value = null
            })
    }

    /**
     * 请求图典一级分类
     */
    fun getDictionaryRootClassify(){
        request(
            { apiService.getDictionaryRootClassify()},
            {
                //请求成功
                classifyList.value = it
            },
            {
                //请求失败
                classifyList.value = null
            })
    }

    /**
     * 图典指定分类的子类
     */
    fun getDictionaryClassifyById(id: String){
        request(
            { apiService.getDictionaryClassifyById(id)},
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