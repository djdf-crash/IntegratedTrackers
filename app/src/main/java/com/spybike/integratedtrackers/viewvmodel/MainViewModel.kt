package com.spybike.integratedtrackers.viewvmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spybike.integratedtrackers.database.AppDatabase
import com.spybike.integratedtrackers.models.DeviceModel
import com.spybike.integratedtrackers.models.FilterModel
import com.spybike.integratedtrackers.models.PointMarkerModels
import com.spybike.integratedtrackers.models.UserAccountInfoModel
import com.spybike.integratedtrackers.repo.DataRepository
import kotlinx.coroutines.Job

class MainViewModel : ViewModel() {

    private val repo: DataRepository = DataRepository()
    private var loginLiveData: MutableLiveData<Map<String, String>> = MutableLiveData()
    private var userInfoLiveData: MutableLiveData<UserAccountInfoModel> = MutableLiveData()
    private var userDevicesLiveData: MutableLiveData<List<DeviceModel>> = MutableLiveData()
    private var listPointMarkerLiveData: MutableLiveData<ArrayList<PointMarkerModels>> = MutableLiveData()
    private var selectDeviceLiveData: MutableLiveData<DeviceModel> = MutableLiveData()

    private var jobLogin: Job? = null

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
        if (jobLogin == null || jobLogin?.isCompleted!!){
            jobLogin = repo.login(userName, password, loginLiveData)
        }
    }

    fun getFilterLiveData(ctx: Context): LiveData<FilterModel>? {
        return AppDatabase.getAppDataBase(ctx)?.filterDAO()?.getOneFiltered()
    }

    fun getDataListFromWeb() : LiveData<ArrayList<PointMarkerModels>>{
        return listPointMarkerLiveData
    }

    fun updateDataListFromWeb(filter: FilterModel?) {
        if (filter != null) {
            repo.getListDataByFilter(filter, listPointMarkerLiveData)
        }
    }

    fun setSelectDeviceUser(selectDevice: DeviceModel){
        selectDeviceLiveData.postValue(selectDevice)
    }

    fun getSelectDeviceUserLiveData(): LiveData<DeviceModel>{
        return selectDeviceLiveData
    }

    fun applyFilter(ctx: Context?, selectFilter: FilterModel) {
        if (ctx != null) {
            AppDatabase.getAppDataBase(ctx)?.applyFilterDatabase(selectFilter)
        }
    }
}
