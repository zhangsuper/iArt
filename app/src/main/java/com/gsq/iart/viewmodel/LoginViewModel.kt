package com.gsq.iart.viewmodel

import androidx.lifecycle.MutableLiveData
import com.gsq.iart.app.network.apiService
import com.gsq.iart.app.network.stateCallback.UpdateUiState
import com.gsq.iart.data.bean.UserInfo
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.request

class LoginViewModel : BaseViewModel() {

    var loginResultDataState = MutableLiveData<UpdateUiState<UserInfo>>()
    var logoutResultDataState = MutableLiveData<UpdateUiState<Any>>()


    fun loginByWeChat(code: String) {
        request(
            { apiService.loginByWechat(code) },
            {
                //请求成功
                val updateDataUiState = UpdateUiState(
                    isSuccess = true,
                    data = it,
                )
                loginResultDataState.value = updateDataUiState
            },
            {
                //请求失败
                val updateDataUiState = UpdateUiState<UserInfo>(
                    isSuccess = false,
                    errorMsg = it.errorMsg
                )
                loginResultDataState.value = updateDataUiState
            }, isShowDialog = true
        )
    }

    fun logout() {
        request(
            { apiService.logout() },
            {
                //请求成功
                val updateDataUiState = UpdateUiState(
                    isSuccess = true,
                    data = it,
                )
                logoutResultDataState.value = updateDataUiState
            },
            {
                //请求失败
                val updateDataUiState = UpdateUiState<Any>(
                    isSuccess = false,
                    errorMsg = it.errorMsg
                )
                logoutResultDataState.value = updateDataUiState
            }, isShowDialog = true
        )
    }

    fun getUserInfo() {
        request(
            { apiService.getUserInfo() },
            {
                //请求成功
                val updateDataUiState = UpdateUiState(
                    isSuccess = true,
                    data = it,
                )
                loginResultDataState.value = updateDataUiState
            },
            {
                //请求失败
                val updateDataUiState = UpdateUiState<UserInfo>(
                    isSuccess = false,
                    errorMsg = it.errorMsg
                )
                loginResultDataState.value = updateDataUiState
            })
    }
}