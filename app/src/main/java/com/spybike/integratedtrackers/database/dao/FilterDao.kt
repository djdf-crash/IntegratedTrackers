package com.spybike.integratedtrackers.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.spybike.integratedtrackers.models.FilterModel
import com.spybike.integratedtrackers.utils.AppConstants.TABLE_FILTER_LAST_CHANGE
import com.spybike.integratedtrackers.utils.AppConstants.TABLE_FILTER_NAME

@Dao
interface FilterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilter(filter: FilterModel)

    @Update
    fun updateGender(filter: FilterModel)

    @Query("SELECT * FROM $TABLE_FILTER_NAME order by $TABLE_FILTER_NAME.$TABLE_FILTER_LAST_CHANGE limit 1")
    fun getOneFiltered(): LiveData<FilterModel>

    @Query("SELECT * FROM $TABLE_FILTER_NAME")
    fun getAllFiltered(): List<FilterModel>
}