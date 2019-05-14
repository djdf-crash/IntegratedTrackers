package com.spybike.integratedtrackers.viewvmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.spybike.integratedtrackers.database.AppDatabase
import com.spybike.integratedtrackers.models.FilterModel
import com.spybike.integratedtrackers.models.PointMarkerModels
import com.spybike.integratedtrackers.repo.DataRepository

class MapViewModel : ViewModel() {

    private val repo: DataRepository = DataRepository()
    private var listPointMarkerLiveData: MutableLiveData<List<PointMarkerModels>> = MutableLiveData()

    fun getFilterLiveData(ctx: Context): LiveData<FilterModel>? {
        return AppDatabase.getAppDataBase(ctx)?.filterDAO()?.getOneFiltered()
    }

    fun getDataListFromWeb(filter: FilterModel): LiveData<List<PointMarkerModels>>{
        repo.getListDataByFilter(filter, listPointMarkerLiveData)
        return listPointMarkerLiveData
    }
}
