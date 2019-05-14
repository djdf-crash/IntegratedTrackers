package com.spybike.integratedtrackers.enums

enum class Filter(val mode: String) {
    MOST_RECENT("recent"),
    TODAY("today"),
    DATE("date"),
    MONTH("month"),
    BETWEEN("daterange")
}