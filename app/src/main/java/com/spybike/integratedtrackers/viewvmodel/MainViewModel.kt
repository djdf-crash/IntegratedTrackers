package com.spybike.integratedtrackers.viewvmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spybike.integratedtrackers.models.DeviceModel
import com.spybike.integratedtrackers.models.UserAccountInfoModel
import com.spybike.integratedtrackers.repo.DataRepository

class MainViewModel : ViewModel() {

    private val repo: DataRepository = DataRepository()
    private var loginLiveData: MutableLiveData<Map<String, String>> = MutableLiveData()
    private var userInfoLiveData: MutableLiveData<UserAccountInfoModel> = MutableLiveData()
    private var userDevicesLiveData: MutableLiveData<List<DeviceModel>> = MutableLiveData()

    fun getUserDevicesLiveData(): LiveData<List<DeviceModel>>{
        return userDevicesLiveData
    }

    fun getUserInfoLiveData(): LiveData<UserAccountInfoModel>{
        return userInfoLiveData
    }

    fun updateUserInfo(){
        repo.getUserAccountInfo(userInfoLiveData)
    }

    fun updateDevicesUserInfo(){
        repo.getUserDevices(userDevicesLiveData)
    }

    fun getLoginLiveData(): LiveData<Map<String, String>>{
        return loginLiveData
    }

    fun login(userName: String, password: String){
        repo.login(userName, password, loginLiveData)
    }
}
