package com.spybike.integratedtrackers.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.spybike.integratedtrackers.models.FilterModel
import java.text.SimpleDateFormat
import java.util.*


object Connections {

    var context: Context? = null

    fun isConnectingToInternet(): Boolean {
        if (context != null) {
            val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            return activeNetwork?.isConnectedOrConnecting == true
        }
        return false
    }

    fun getParametersURL(filter: FilterModel): String {
        val today = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
        return "?unit_code=${filter.selectedDevice?.unitCode}" +
                "&today=$today" +
                "&date=${filter.date}" +
                "&num_rows=${filter.numberRows}" +
                "&date_from=${filter.dateFrom}%2000:00" +
                "&date_to=${filter.dateTo}%2023:59" +
//              "&month=${filter.month}" +
                "&month=0" +
                "&year=2019" +
//              "&year=${filter.year}" +
                "&mode=${filter.selectMode?.mode}" +
                "&func=undefined"
    }

}