package com.spybike.integratedtrackers.views.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.spybike.integratedtrackers.R
import com.spybike.integratedtrackers.adapters.ImageAdapter
import com.spybike.integratedtrackers.models.DeviceModel
import com.spybike.integratedtrackers.models.FilterModel
import com.spybike.integratedtrackers.viewvmodel.MainViewModel
import kotlinx.android.synthetic.main.image_fragment.*

class ImageFragment : Fragment() {

    companion object {
        fun newInstance() = ImageFragment()
    }

    private lateinit var viewModel: MainViewModel
    private var mFilter: FilterModel? = null
    private var mSelectedDevice: DeviceModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.image_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        }?: throw Exception("Invalid Activity")

        rw.layoutManager = LinearLayoutManager(this.context)
        rw.adapter = ImageAdapter(mFilter)

        subscribeViewModel()
    }

    private fun subscribeViewModel() {
        viewModel.getFilterLiveData(activity as Context)?.observe(this, Observer { filter ->
            mFilter = filter
            mFilter?.selectedDevice = mSelectedDevice
            (rw.adapter as ImageAdapter).setNewFilter(mFilter)
        })

        viewModel.getSelectDeviceUserLiveData().observe(this, Observer {
            mSelectedDevice = it
            mFilter?.selectedDevice = mSelectedDevice
            (rw.adapter as ImageAdapter).setNewFilter(mFilter)
        })
    }

}
