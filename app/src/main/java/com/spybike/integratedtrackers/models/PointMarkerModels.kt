package com.spybike.integratedtrackers.models

import com.google.android.gms.maps.model.LatLng
import java.util.*

data class PointMarkerModels (
    var id: String,
    var date: Date,
    var time: String,
    var latLng: LatLng,
    var reason: String
) {
}