package com.spybike.integratedtrackers.repo

import androidx.lifecycle.MutableLiveData
import com.spybike.integratedtrackers.models.FilterModel
import com.spybike.integratedtrackers.models.PointMarkerModels
import com.spybike.integratedtrackers.utils.AppConstants
import org.jsoup.Jsoup

class DataRepository {

    fun getListDataByFilter(filter: FilterModel, listPointMarkerLiveData: MutableLiveData<List<PointMarkerModels>>) {
        var list = listPointMarkerLiveData.value
        if (list == null){
            list = ArrayList<PointMarkerModels>()
        }

        Jsoup.connect(convertObjectToParametrsForUrl(filter))

    }

    fun convertObjectToParametrsForUrl(filter: FilterModel): String{
        return AppConstants.LIST_DATA_URL
    }

}