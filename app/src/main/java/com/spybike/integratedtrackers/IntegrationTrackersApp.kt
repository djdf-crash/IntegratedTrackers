package com.spybike.integratedtrackers

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.spybike.integratedtrackers.utils.Connections

class IntegrationTrackersApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Connections.context = this
        Fresco.initialize(this)
    }
}