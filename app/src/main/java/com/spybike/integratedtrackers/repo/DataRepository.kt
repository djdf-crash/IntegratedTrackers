package com.spybike.integratedtrackers.repo

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.spybike.integratedtrackers.R
import com.spybike.integratedtrackers.models.DeviceModel
import com.spybike.integratedtrackers.models.FilterModel
import com.spybike.integratedtrackers.models.PointMarkerModels
import com.spybike.integratedtrackers.models.UserAccountInfoModel
import com.spybike.integratedtrackers.utils.AppConstants
import com.spybike.integratedtrackers.utils.Connections
import com.spybike.integratedtrackers.utils.PreferenceHelper
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.stfalcon.frescoimageviewer.ImageViewer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class DataRepository {

    fun openImageByFilter(view: ImageView?, filter: FilterModel, baseURL: String) {
        ImageViewer.Builder(view?.context, listOf(baseURL + Connections.getParametersURL(filter))).show()
    }

    fun getImageByFilter(view: ImageView, progressBar: ProgressBar, filter: FilterModel, baseURL: String){

        Picasso.get()
            .load(baseURL + Connections.getParametersURL(filter))
            .error(R.drawable.ic_error)
            .into(object : Target{
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    view.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    progressBar.visibility = View.GONE
                    view.setImageDrawable(errorDrawable)
                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    view.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    view.setImageBitmap(bitmap)
                }

            })

    }

    fun getListDataByFilter(filter: FilterModel, listPointMarkerLiveData: MutableLiveData<ArrayList<PointMarkerModels>>) {

        if (PreferenceHelper.cookies == null) {
            listPointMarkerLiveData.postValue(null)
            return
        }

        GlobalScope.launch {
            if (Connections.isConnectingToInternet() && filter.selectedDevice != null) {
                val url: String = AppConstants.LOCATION_LIST_URL + Connections.getParametersURL(filter)
                var list: ArrayList<PointMarkerModels>? = listPointMarkerLiveData.value
                if (list == null) {
                    list = ArrayList()
                }else{
                    list.clear()
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
                                    var arr = listTdElements[2].text().split(" ")
                                    val lat = arr[0].toDouble() + (arr[1].toDouble() / 60.0)
                                    arr = listTdElements[3].text().split(" ")
                                    val lng = arr[0].toDouble() + (arr[1].toDouble() / 60.0)
                                    list.add(PointMarkerModels(
                                        id,
                                        listTdElements[0].text(),
                                        listTdElements[1].text(),
                                        LatLng(lat, lng),
                                        listTdElements[4].text())
                                    )
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
            if (Connections.isConnectingToInternet()) {
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
            if (Connections.isConnectingToInternet()) {
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
            if (Connections.isConnectingToInternet()) {
                val res = Jsoup.connect("${AppConstants.BASE_URL}${AppConstants.LOGIN_URL}")
                    .data("user_name", userName, "password", password)
                    .method(Connection.Method.POST)
                    .execute()
                val doc = res.parse()
                if (doc.select("error").size == 0) {
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