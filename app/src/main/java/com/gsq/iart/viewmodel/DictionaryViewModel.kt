package com.gsq.iart.viewmodel

import androidx.lifecycle.MutableLiveData
import com.gsq.iart.app.network.apiService
import com.gsq.iart.app.network.stateCallback.ListDataUiState
import com.gsq.iart.data.Constant
import com.gsq.iart.data.bean.DictionaryMenuBean
import com.gsq.iart.data.bean.DictionarySetsBean
import com.gsq.iart.data.bean.DictionaryWorksBean
import com.gsq.iart.data.request.CompareAddRequestParam
import com.gsq.iart.data.request.DictionaryWoksRequestParam
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.request

class DictionaryViewModel : BaseViewModel() {

    //页码
    var pageNo = 1

    var classifyList: MutableLiveData<ArrayList<DictionaryMenuBean>> = MutableLiveData()
    var classifySubList: MutableLiveData<ArrayList<DictionaryMenuBean>> = MutableLiveData()

    //作品列表
    var worksDataState: MutableLiveData<ListDataUiState<DictionaryWorksBean>> = MutableLiveData()
    var classifyFourSubList: MutableLiveData<ArrayList<DictionaryMenuBean>> =
        MutableLiveData()//四级标签

    //图单列表
    var comparePageDataState: MutableLiveData<ListDataUiState<DictionarySetsBean>> =
        MutableLiveData()

    //图单里对比列表
    var compareItemPageDataState: MutableLiveData<ListDataUiState<DictionaryWorksBean>> =
        MutableLiveData()

    //添加图单
    var addComparePageLiveData: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * 请求图典分类列表
     */
    fun getDictionaryClassifyList() {
        request(
            { apiService.getDictionaryClassify() },
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
    fun getDictionaryRootClassify() {
        request(
            { apiService.getDictionaryRootClassify() },
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
    fun getDictionaryClassifyById(id: Int) {
        request(
            { apiService.getDictionaryClassifyById(id) },
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
     * 图典指定分类的子类
     */
    fun getDictionaryFourClassifyById(id: Int) {
        request(
            { apiService.getDictionaryClassifyById(id) },
            {
                //请求成功
                classifyFourSubList.value = it
            },
            {
                //请求失败
                classifyFourSubList.value = null
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
                        listData = arrayListOf<DictionaryWorksBean>()
                    )
                worksDataState.value = listDataUiState
            })
    }

    /**
     * 图单列表
     */
    fun findComparePage(isRefresh: Boolean) {
        if (isRefresh) {
            pageNo = 1
        }
        request(
            { apiService.findComparePage(pageNo, Constant.DEFAULT_REQUEST_SIZE) },
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
                comparePageDataState.value = listDataUiState
            },
            {
                //请求失败
                val listDataUiState =
                    ListDataUiState(
                        isSuccess = false,
                        errMessage = it.errorMsg,
                        isRefresh = isRefresh,
                        listData = arrayListOf<DictionarySetsBean>()
                    )
                comparePageDataState.value = listDataUiState
            })
    }

    fun findCompareItemPage(isRefresh: Boolean, id: Long) {
        if (isRefresh) {
            pageNo = 1
        }
        request(
            { apiService.findCompareItemPage(id, pageNo, Constant.DEFAULT_REQUEST_SIZE) },
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
                compareItemPageDataState.value = listDataUiState
            },
            {
                //请求失败
                val listDataUiState =
                    ListDataUiState(
                        isSuccess = false,
                        errMessage = it.errorMsg,
                        isRefresh = isRefresh,
                        listData = arrayListOf<DictionaryWorksBean>()
                    )
                compareItemPageDataState.value = listDataUiState
            })
    }

    fun addCompare(name: String, ids: MutableList<Int>) {
        var requestParam = CompareAddRequestParam(
            name, ids
        )
        request(
            { apiService.addCompare(requestParam) },
            {
                //请求成功
                addComparePageLiveData.value = true
            },
            {
                //请求失败
                addComparePageLiveData.value = false
            })
    }
}