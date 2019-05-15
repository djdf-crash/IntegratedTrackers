package com.spybike.integratedtrackers

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.spybike.integratedtrackers.utils.AppConstants

class App: Application() {

    var mSharedPref: SharedPreferences = getSharedPreferences(AppConstants.SHARED_NAME, Context.MODE_PRIVATE)


    override fun onCreate() {
        super.onCreate()

    }

    fun getSharedPref(): SharedPreferences{
        return mSharedPref
    }
}