package com.spybike.integratedtrackers.models

import com.google.android.gms.maps.model.LatLng

data class PointMarkerModels (
    var id: String,
    var date: String,
    var time: String,
    var latLng: LatLng,
    var reason: String
) {
    constructor(): this("", "", "", LatLng(0.0,0.0), "")
}