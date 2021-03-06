package com.spybike.integratedtrackers.utils

object AppConstants {

    const val APP_DATABASE: String = "app_database"
    const val APP_DATABASE_VERSION: Int = 1

    const val TABLE_FILTER_NAME: String = "table_filter"
    const val TABLE_FILTER_ID: String = "id"
    const val TABLE_FILTER_SELECT_FILTER: String = "selected"
    const val TABLE_FILTER_DATE: String = "date"
    const val TABLE_FILTER_YEAR: String = "year"
    const val TABLE_FILTER_MONTH: String = "month"
    const val TABLE_FILTER_DATE_FROM: String = "date_from"
    const val TABLE_FILTER_DATE_TO: String = "date_to"
    const val TABLE_FILTER_NUMBER_ROWS: String = "num_rows"
    const val TABLE_FILTER_NAME_FILTER: String = "name"
    const val TABLE_FILTER_LAST_CHANGE: String = "last_change"

    const val BASE_URL: String = "http://www.integratedtrackers.com/GPSTrack"
    const val LOCATION_LIST_URL: String = "$BASE_URL/LocationsList"
    const val BATTERY_URL: String = "$BASE_URL/Battery"
    const val GSM_URL: String = "$BASE_URL/GSMSignal"
    const val VELOCITY_URL: String = "$BASE_URL/Velocity"
    const val ACCOUNT_BALANCE_URL: String = "/api/get_account_balance"
    const val LOGIN_URL: String = "/api/login"

    const val SHARED_NAME: String = "app_shared"
    const val SHARED_COOKIES: String = "cookies"

}