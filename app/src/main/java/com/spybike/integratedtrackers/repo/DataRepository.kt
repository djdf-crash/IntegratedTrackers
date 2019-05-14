package com.spybike.integratedtrackers.repo

import androidx.lifecycle.MutableLiveData
import com.spybike.integratedtrackers.models.FilterModel
import com.spybike.integratedtrackers.models.PointMarkerModels
import com.spybike.integratedtrackers.utils.AppConstants
import org.jsoup.Jsoup
import java.util.*

class DataRepository {

    fun getListDataByFilter(filter: FilterModel, listPointMarkerLiveData: MutableLiveData<List<PointMarkerModels>>) {

        val url: String = "${AppConstants.BASE_URL}/LocationsList?unit_code=${filter.id}" +
                "&today=${Calendar.getInstance().time}" +
                "&date=${filter.date}" +
                "&num_rows=${filter.numberRows}" +
                "&date_from=${filter.dateFrom}" +
                "&date_to=${filter.dateTo}" +
                "&month=${filter.month}" +
                "&year=${filter.year}" +
                "&mode=${filter.selectMode?.name}" +
                "&func=undefined"

        var list = listPointMarkerLiveData.value
        if (list == null){
            list = ArrayList<PointMarkerModels>()
        }

        Jsoup.connect(url).get().run {
            select("tr").forEachIndexed { index, element ->
                println("$index. $element")
            }
        }

    }

}