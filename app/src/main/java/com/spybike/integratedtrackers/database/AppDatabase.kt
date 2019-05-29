package com.spybike.integratedtrackers.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.spybike.integratedtrackers.database.dao.FilterDao
import com.spybike.integratedtrackers.models.FilterModel
import com.spybike.integratedtrackers.utils.AppConstants
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(entities = [FilterModel::class], exportSchema = false, version = AppConstants.APP_DATABASE_VERSION)
@TypeConverters(DateTypeConverter::class, TypeFilterConverter::class)
abstract class AppDatabase :  RoomDatabase() {

    abstract fun filterDAO(): FilterDao

    fun applyFilterDatabase(filter: FilterModel){
        GlobalScope.launch {
            INSTANCE?.filterDAO()?.insertFilter(filter)
        }
    }

    companion object {
        var INSTANCE: AppDatabase? = null

        fun getAppDataBase(context: Context): AppDatabase? {
            if (INSTANCE == null){
                synchronized(AppDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, AppConstants.APP_DATABASE).build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }

}