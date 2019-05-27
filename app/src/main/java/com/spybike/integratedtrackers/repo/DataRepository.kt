package com.spybike.integratedtrackers.repo

import androidx.lifecycle.MutableLiveData
import com.spybike.integratedtrackers.models.DeviceModel
import com.spybike.integratedtrackers.models.FilterModel
import com.spybike.integratedtrackers.models.PointMarkerModels
import com.spybike.integratedtrackers.models.UserAccountInfoModel
import com.spybike.integratedtrackers.utils.AppConstants
import com.spybike.integratedtrackers.utils.ConnectionDetector
import com.spybike.integratedtrackers.utils.PreferenceHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DataRepository {

    fun getListDataByFilter(filter: FilterModel, listPointMarkerLiveData: MutableLiveData<List<PointMarkerModels>>) {

        if (PreferenceHelper.cookies == null) {
            listPointMarkerLiveData.postValue(ArrayList())
            return
        }

        GlobalScope.launch {
            val url: String = "${AppConstants.BASE_URL}/LocationsList?unit_code=${filter.id}" +
                    "&today=${Calendar.getInstance().time}" +
                    "&date=${filter.date}" +
                    "&num_rows=${filter.numberRows}" +
                    "&date_from=${filter.dateFrom}" +
                    "&date_to=${filter.dateTo}" +
                    "&month=${filter.month}" +
                    "&year=${filter.year}" +
                    "&mode=${filter.selectMode?.name}" +
                    "&func=undefined"

            var list = listPointMarkerLiveData.value
            if (list == null) {
                list = ArrayList()
            }

            Jsoup.connect(url)
                .cookies(PreferenceHelper.cookies)
                .get()
                .run {
                    select("tr").forEachIndexed { index, element ->
                        println("$index. $element")
                    }
                }
        }

    }

    fun getUserDevices(userDevicesLiveData: MutableLiveData<List<DeviceModel>>) {
        if (PreferenceHelper.cookies == null) {
            userDevicesLiveData.postValue(null)
            return
        }

        GlobalScope.launch {
            val listDevices: ArrayList<DeviceModel> = ArrayList()
            listDevices.add(DeviceModel())
            Jsoup.connect("${AppConstants.BASE_URL}/api/getdevices")
                .cookies(PreferenceHelper.cookies)
                .get()
                .run {
                    val elements = select("row")
                    elements.forEach {el ->
                        listDevices.add(DeviceModel(
                            el.select("id").text(),
                            el.select("account_id").text(),
                            el.select("nickname").text(),
                            el.select("unit_code").text()))
                    }
                }
            userDevicesLiveData.postValue(listDevices)
        }
    }

    fun getUserAccountInfo(accountInfo: MutableLiveData<UserAccountInfoModel>){
        if (PreferenceHelper.cookies == null) {
            accountInfo.postValue(null)
            return
        }

        GlobalScope.launch {
            if (ConnectionDetector.isConnectingToInternet()) {
                Jsoup.connect("${AppConstants.BASE_URL}${AppConstants.ACCOUNT_BALANCE_URL}")
                    .cookies(PreferenceHelper.cookies)
                    .get()
                    .run {
                        val element = select("row")
                        if (element.size != 0) {
                            accountInfo.postValue(
                                UserAccountInfoModel(
                                    element.select("user_name").text(),
                                    element.select("balance").text(),
                                    element.select("currency").text()
                                )
                            )
                        }
                    }
            }

        }

    }

    fun login(userName: String, password: String, loginLiveData: MutableLiveData<Map<String, String>>): Job {
        return GlobalScope.launch {
            if (ConnectionDetector.isConnectingToInternet()) {
                val res = Jsoup.connect("${AppConstants.BASE_URL}${AppConstants.LOGIN_URL}")
                    .data("user_name", userName, "password", password)
                    .method(Connection.Method.POST)
                    .execute()
                val doc = res.parse()
                if (doc.select("error").size == 0) {
                    res.cookies()[AppConstants.SHARED_USER] = userName
                    res.cookies()[AppConstants.SHARED_PASSWORD] = password
                    loginLiveData.postValue(res.cookies())
                } else {
                    res.cookies()["error"] = doc.select("error").text()
                    loginLiveData.postValue(res.cookies())
                }
            }else{
                val mapError = HashMap<String, String>()
                mapError["error"] = "No internet connection!"
                loginLiveData.postValue(mapError)
            }
        }
    }

}