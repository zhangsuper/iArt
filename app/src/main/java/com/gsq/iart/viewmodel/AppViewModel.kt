package com.gsq.iart.viewmodel

import androidx.lifecycle.MutableLiveData
import com.gsq.iart.app.network.apiService
import com.gsq.iart.app.network.stateCallback.UpdateUiState
import com.gsq.iart.data.bean.AppVersion
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.request

class AppViewModel : BaseViewModel() {

    var appVersionDataState = MutableLiveData<UpdateUiState<AppVersion>>()

    fun checkAppVersion(versionCode: String) {
        request(
            { apiService.checkAppVersion(versionCode) },
            {
                //请求成功
                val updateDataUiState = UpdateUiState(
                    isSuccess = true,
                    data = it,
                )
                appVersionDataState.value = updateDataUiState
            },
            {
                //请求失败
                val updateDataUiState = UpdateUiState<AppVersion>(
                    isSuccess = false,
                    errorMsg = it.errorMsg
                )
                appVersionDataState.value = updateDataUiState
            }, isShowDialog = false
        )
    }


}