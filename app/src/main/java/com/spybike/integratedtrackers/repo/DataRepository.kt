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
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DataRepository {

    fun getListDataByFilter(filter: FilterModel, listPointMarkerLiveData: MutableLiveData<ArrayList<PointMarkerModels>>) {

        if (PreferenceHelper.cookies == null) {
            listPointMarkerLiveData.postValue(ArrayList())
            return
        }

        GlobalScope.launch {
            if (ConnectionDetector.isConnectingToInternet() && filter.selectedDevice != null) {
                val today = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
                val url: String = "${AppConstants.BASE_URL}/LocationsList?unit_code=${filter.selectedDevice?.unitCode}" +
                        "&today=$today" +
                        "&date=${filter.date}" +
                        "&num_rows=${filter.numberRows}" +
                        "&date_from=${filter.dateFrom}%2000:00" +
                        "&date_to=${filter.dateTo}%2023:59" +
//                        "&month=${filter.month}" +
                        "&month=0" +
                        "&year=2019" +
//                        "&year=${filter.year}" +
                        "&mode=${filter.selectMode?.mode}" +
                        "&func=undefined"

                var list: ArrayList<PointMarkerModels>? = listPointMarkerLiveData.value
                if (list == null) {
                    list = ArrayList()
                }

                Jsoup.connect(url)
                    .cookies(PreferenceHelper.cookies)
                    .get()
                    .run {
                        select("tr").forEachIndexed { index, element ->
                            if (index > 0) {
                                val listTdElements = element.select("td")
                                if (listTdElements.size >= 7) {
                                    val id = (listTdElements[6].childNode(0) as Element).attr("onClick").substringAfter("(").substringBefore(")")
//                                    list.add(PointMarkerModels(id, LocalDate.parse(listTdElements[0].text()),listTdElements[1].text(), LatLng()))
                                }
                            }
                        }
                    }
                listPointMarkerLiveData.postValue(list)
            }
        }

    }

//    <tr style="background-color:whitesmoke">
//    <td>2019-03-17</td>
//    <td>18:57:07.0</td>
//    <td>+50 25.3896</td>
//    <td>+030 23.3610</td>
//    <td>VIB_PERIOD</td>
//    <td><a target="blank" href="
//    http://maps.google.co.uk/maps?q=+50 25.3896,+030 23.3610"> Show On Map </a></td>
//    <td align="center" style="cursor:pointer"><img src="images/mag.gif" onclick="PopUpExtended(2322169)"><img src="images/delete.bmp" onclick="DeleteLocation(2322169)"></td>
//    </tr>

//    <tr style="background-color: silver;width: 100%; font-weight : bold">
//    <td>Date</td>
//    <td>Time</td>
//    <td>Latitude</td>
//    <td>Longitude</td>
//    <td>Reason</td>
//    <td>Google Maps</td>
//    <td>Extra</td>
//    </tr>

    fun getUserDevices(userDevicesLiveData: MutableLiveData<List<DeviceModel>>) {
        if (PreferenceHelper.cookies == null) {
            userDevicesLiveData.postValue(null)
            return
        }

        GlobalScope.launch {
            val listDevices: ArrayList<DeviceModel> = ArrayList()
            if (ConnectionDetector.isConnectingToInternet()) {
                Jsoup.connect("${AppConstants.BASE_URL}/api/getdevices")
                    .cookies(PreferenceHelper.cookies)
                    .get()
                    .run {
                        val elements = select("row")
                        elements.forEach { el ->
                            listDevices.add(
                                DeviceModel(
                                    el.select("id").text(),
                                    el.select("account_id").text(),
                                    el.select("nickname").text(),
                                    el.select("unit_code").text()
                                )
                            )
                        }
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