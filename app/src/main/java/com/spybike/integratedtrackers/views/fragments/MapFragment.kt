package com.spybike.integratedtrackers.views.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.spybike.integratedtrackers.R
import com.spybike.integratedtrackers.models.DeviceModel
import com.spybike.integratedtrackers.models.FilterModel
import com.spybike.integratedtrackers.models.PointMarkerModels
import com.spybike.integratedtrackers.viewvmodel.MainViewModel
import permissions.dispatcher.*


@RuntimePermissions
class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var mGoogleMap:GoogleMap? = null
    private var mapView: View? = null
    private var mFilter: FilterModel? = null
    private lateinit var viewModel: MainViewModel
    private var mSelectedDevice: DeviceModel? = null

    companion object {
        fun newInstance(): MapFragment{
            return MapFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.map_fragment, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapView = mapFragment?.view
        mapFragment?.getMapAsync(this)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        }?: throw Exception("Invalid Activity")


        viewModel.getDataListFromWeb().observe(this, Observer {listPointMarket ->
            if (listPointMarket != null){
                addMarkersOnMaps(listPointMarket)
            }else{
                mGoogleMap?.clear()

            }
        })

        viewModel.getFilterLiveData(activity as Context)?.observe(this, Observer {filter ->
            mFilter = filter
            mFilter?.selectedDevice = mSelectedDevice
            viewModel.updateDataListFromWeb(mFilter)
        })

        viewModel.getSelectDeviceUserLiveData().observe(this, Observer {
            mSelectedDevice = it
            mFilter?.selectedDevice = mSelectedDevice
            viewModel.updateDataListFromWeb(mFilter)
        })

    }

    private fun addMarkersOnMaps(listPointMarket: List<PointMarkerModels>) {
        if (listPointMarket.isEmpty()){
            mGoogleMap?.clear()
            return
        }
    }

    override fun onMapReady(map: GoogleMap) {

        mGoogleMap = map
        mGoogleMap?.setOnMarkerClickListener(this)
        mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL

        accessFineLocationWithPermissionCheck()

        if (mapView != null && mapView?.findViewById<View>(Integer.parseInt("1")) != null) {
            // Get the button view
            val locationButton =
                ((mapView?.findViewById<View>(Integer.parseInt("1")) as View).parent as View).findViewById<View>(Integer.parseInt("2"))
            // and next place it, on bottom right (as Google Maps app)
            val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 30, 30)
        }

    }

    fun updateMarkersGoogleMap(){
        viewModel.updateDataListFromWeb(mFilter)
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun accessFineLocation() {
        mGoogleMap?.isMyLocationEnabled = true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    fun permissionDenied() {
        Toast.makeText(activity, "Permission Denied", Toast.LENGTH_LONG).show()
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    fun neverAskAgain() {
        Toast.makeText(activity, "Never Ask again!", Toast.LENGTH_LONG).show()
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    fun showRationaleForCamera(request: PermissionRequest) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        showRationaleDialog(R.string.permission_fine_location_rationale, request)
    }

    private fun showRationaleDialog(messageResId: Int, request: PermissionRequest) {
        AlertDialog.Builder(activity as Context)
            .setPositiveButton(R.string.button_allow) { _, _ -> request.proceed() }
            .setNegativeButton(R.string.button_deny) { _, _ -> request.cancel() }
            .setCancelable(false)
            .setMessage(messageResId)
            .show()
    }
}
