package com.spybike.integratedtrackers.viewvmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.spybike.integratedtrackers.repo.DataRepository

class MainViewModel : ViewModel() {

    private val repo: DataRepository = DataRepository()
    private var loginLiveData: MutableLiveData<Map<String, String>> = MutableLiveData()

    fun getLoginLiveData(): LiveData<Map<String, String>>{
        return loginLiveData
    }

    fun login(userName: String, password: String){
        repo.login(userName, password, loginLiveData)
    }
}
