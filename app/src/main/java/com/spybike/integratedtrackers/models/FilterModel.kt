package com.spybike.integratedtrackers.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.spybike.integratedtrackers.enums.Filter
import com.spybike.integratedtrackers.utils.AppConstants
import java.util.*

@Entity(tableName = AppConstants.TABLE_FILTER_NAME)
data class FilterModel (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = AppConstants.TABLE_FILTER_ID) var id: Int?,
    @ColumnInfo(name = AppConstants.TABLE_FILTER_SELECT_FILTER)var selectMode: Filter?,
    @ColumnInfo(name = AppConstants.TABLE_FILTER_DATE) var date: Date,
    @ColumnInfo(name = AppConstants.TABLE_FILTER_YEAR) var year: String,
    @ColumnInfo(name = AppConstants.TABLE_FILTER_MONTH) var month: Int,
    @ColumnInfo(name = AppConstants.TABLE_FILTER_DATE_FROM) var dateFrom: Date,
    @ColumnInfo(name = AppConstants.TABLE_FILTER_DATE_TO) var dateTo: Date,
    @ColumnInfo(name = AppConstants.TABLE_FILTER_NUMBER_ROWS) var numberRows: Int,
    @ColumnInfo(name = AppConstants.TABLE_FILTER_NAME_FILTER) var nameFiltered: String,
    @ColumnInfo(name = AppConstants.TABLE_FILTER_LAST_CHANGE) var lastChange: Date
) {
    constructor(): this(null, null, Date(), "", 0, Date(), Date(), 100, "", Date())
}