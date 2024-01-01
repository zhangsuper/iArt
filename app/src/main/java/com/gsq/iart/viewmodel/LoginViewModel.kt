package com.gsq.iart.viewmodel

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.gsq.iart.R
import com.gsq.iart.app.network.apiService
import com.gsq.iart.app.network.stateCallback.UpdateUiState
import com.gsq.iart.data.bean.UserInfo
import com.gsq.iart.data.event.LoginSuccessEvent
import com.gsq.mvvm.base.viewmodel.BaseViewModel
import com.gsq.mvvm.ext.request
import org.greenrobot.eventbus.EventBus

class LoginViewModel : BaseViewModel() {

    var loginResultDataState = MutableLiveData<UpdateUiState<UserInfo>>()
    var logoutResultDataState = MutableLiveData<UpdateUiState<Any>>()
    var writeOffDataState = MutableLiveData<UpdateUiState<Any>>()


    fun loginByWeChat(code: String) {
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(StringUtils.getString(R.string.http_error_net_disable))
            return
        }
        request(
            { apiService.loginByWechat(code) },
            {
                //请求成功
                val updateDataUiState = UpdateUiState(
                    isSuccess = true,
                    data = it,
                )
                loginResultDataState.value = updateDataUiState
                EventBus.getDefault().post(LoginSuccessEvent())
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

    /**
     * 注销
     */
    fun writeOff() {
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(StringUtils.getString(R.string.http_error_net_disable))
            return
        }
        request(
            { apiService.writeOff() },
            {
                //请求成功
                val updateDataUiState = UpdateUiState(
                    isSuccess = true,
                    data = it,
                )
                writeOffDataState.value = updateDataUiState
            },
            {
                //请求失败
                val updateDataUiState = UpdateUiState<Any>(
                    isSuccess = false,
                    errorMsg = it.errorMsg
                )
                writeOffDataState.value = updateDataUiState
            }, isShowDialog = true
        )
    }

    fun getUserInfo() {
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(StringUtils.getString(R.string.http_error_net_disable))
            return
        }
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