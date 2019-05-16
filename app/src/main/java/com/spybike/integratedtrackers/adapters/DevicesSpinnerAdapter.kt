package com.spybike.integratedtrackers.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.spybike.integratedtrackers.R
import com.spybike.integratedtrackers.models.DeviceModel
import kotlinx.android.synthetic.main.row.view.*


class DevicesSpinnerAdapter(ctx: Context, var textViewResourceId: Int, var listData: List<DeviceModel>): BaseAdapter() {

    private val mInflater: LayoutInflater = LayoutInflater.from(ctx)

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getItem(p0: Int): Any? {
        if (listData.isEmpty()) return null
        return listData[p0]
    }

    override fun getCount(): Int {
        return listData.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(convertView, parent, position, null)
    }

    private fun getCustomView(convertView: View?, parent: ViewGroup?, position: Int, colorRes: Int?): View {
        val view: View
        val vh: ItemRowHolder
        if (convertView == null) {
            view = mInflater.inflate(textViewResourceId, parent, false)
            vh = ItemRowHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemRowHolder
        }

        if (colorRes != null){
            view.setBackgroundResource(colorRes)
        }

        vh.nickName?.text = "${listData[position].nickName}"
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getCustomView(convertView, parent, position, R.color.colorPrimary)
    }

    fun getDataList(): List<DeviceModel>{
        return listData
    }

    fun setDataList(value: List<DeviceModel>) {
        listData = value
        notifyDataSetChanged()
    }

    fun clearDataList(){
        listData = ArrayList<DeviceModel>()
        notifyDataSetChanged()
    }

    private class ItemRowHolder(row: View?) {

        val nickName: TextView? = row?.nickName

    }
}