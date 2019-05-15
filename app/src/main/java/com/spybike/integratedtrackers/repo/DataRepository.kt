package com.spybike.integratedtrackers.repo

import androidx.lifecycle.MutableLiveData
import com.spybike.integratedtrackers.models.FilterModel
import com.spybike.integratedtrackers.models.PointMarkerModels
import com.spybike.integratedtrackers.utils.AppConstants
import com.spybike.integratedtrackers.utils.PreferenceHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.util.*
import kotlin.collections.ArrayList

class DataRepository {

    fun getListDataByFilter(filter: FilterModel, listPointMarkerLiveData: MutableLiveData<List<PointMarkerModels>>) {

        if (PreferenceHelper.cookies == null) {
            listPointMarkerLiveData.postValue(null)
            return
        }

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
        if (list == null){
            list = ArrayList<PointMarkerModels>()
        }

        Jsoup
            .connect(url)
            .cookies(PreferenceHelper.cookies)
            .get()
            .run {
            select("tr").forEachIndexed { index, element ->
                println("$index. $element")
            }
        }

    }

    fun login(userName: String, password: String, loginLiveData: MutableLiveData<Map<String, String>>): Job {
        return GlobalScope.launch {
            val res = Jsoup.connect("${AppConstants.BASE_URL}/api/login")
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
        }
    }

}