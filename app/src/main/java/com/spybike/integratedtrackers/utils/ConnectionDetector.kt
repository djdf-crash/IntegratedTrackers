package com.spybike.integratedtrackers.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object ConnectionDetector {

    var context: Context? = null

    fun isConnectingToInternet(): Boolean {
        if (context != null) {
            val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            return activeNetwork?.isConnectedOrConnecting == true
        }
        return false
    }
}