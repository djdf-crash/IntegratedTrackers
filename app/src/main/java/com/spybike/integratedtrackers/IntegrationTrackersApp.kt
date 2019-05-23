package com.spybike.integratedtrackers

import android.app.Application
import com.spybike.integratedtrackers.utils.ConnectionDetector

class IntegrationTrackersApp: Application() {

    override fun onCreate() {
        super.onCreate()
        ConnectionDetector.context = this
    }
}