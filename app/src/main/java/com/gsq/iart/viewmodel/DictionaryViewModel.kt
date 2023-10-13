package com.gsq.iart.viewmodel

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.gsq.iart.app.network.apiService
import com.gsq.iart.app.network.stateCallback.ListDataUiState
import com.gsq.iart.data.Constant
import com.gsq.iart.data.bean.DictionaryMenuBean
import com.gsq.iart.data.bean.HomeClassifyBean
import com.gsq.iart.data.bean.WorksBean
import com.gsq.iart.data.request.DictionaryWoksRequestParam
import com.gsq.iart.data.request.WorkPageRequestParam
import com.gsq.iart.data.request.WorkPropSearchBean
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.request

class DictionaryViewModel: BaseViewModel() {

    //页码
    var pageNo = 1

    var classifyList: MutableLiveData<ArrayList<DictionaryMenuBean>> = MutableLiveData()
    var classifySubList: MutableLiveData<ArrayList<DictionaryMenuBean>> = MutableLiveData()
    //作品列表
    var worksDataState: MutableLiveData<ListDataUiState<WorksBean>> = MutableLiveData()

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
    fun getDictionaryClassifyById(id: Int){
        request(
            { apiService.getDictionaryClassifyById(id)},
            {
                //请求成功
                classifySubList.value = it
            },
            {
                //请求失败
                classifySubList.value = null
            })
    }

    /**
     * 获取作品列表数据
     */
    fun getDictionaryWorks(
        isRefresh: Boolean,
        searchKey: String,
        tag1: String,
        tag2: String,
        tag3: String,
        tag4: String,
    ) {
        if (isRefresh) {
            pageNo = 1
        }
        var requestParam = DictionaryWoksRequestParam(
            pageNo,
            Constant.DEFAULT_REQUEST_SIZE,
            searchKey,
            tag1, tag2, tag3, tag4
        )
        request(
            { apiService.getDictionaryWorks(requestParam) },
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