package com.spybike.integratedtrackers.database

import androidx.room.TypeConverter
import com.spybike.integratedtrackers.enums.Filter
import java.util.*

class TypeFilterConverter {
    @TypeConverter
    fun fromString(value: String?): Filter? {
        return if (value == null) null else Filter.valueOf(value.toUpperCase())
    }

    @TypeConverter
    fun dateToTimestamp(filter: Filter?): String? {
        return filter?.name
    }
}