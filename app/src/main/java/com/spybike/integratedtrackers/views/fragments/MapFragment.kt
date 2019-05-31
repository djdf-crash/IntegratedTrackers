package com.spybike.integratedtrackers.views.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
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

        subscribeViewModel()

    }

    private fun subscribeViewModel() {
        viewModel.getDataListFromWeb().observe(this, Observer { listPointMarket ->
            if (listPointMarket != null) {
                addMarkersOnMaps(listPointMarket)
            }
        })

        viewModel.getFilterLiveData(activity as Context)?.observe(this, Observer { filter ->
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
        mGoogleMap?.clear()
        if (listPointMarket.isEmpty()){
            Toast.makeText(this.context, "No Locations found for Unit Code: " + mSelectedDevice?.unitCode, Toast.LENGTH_SHORT).show()
            return
        }

        val cameraPosition = CameraPosition.Builder()
            .target(listPointMarket[0].latLng)
            .bearing(45.0f)
            .tilt(30.0f)
            .zoom(16.0f)
            .build()
        mGoogleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        val duration = 1500
        val interpolator = BounceInterpolator()

        val options = PolylineOptions()
        listPointMarket.forEach {

            val start = SystemClock.uptimeMillis()

            val elapsed = SystemClock.uptimeMillis() - start
            val t = Math.max(1 - interpolator.getInterpolation(elapsed.toFloat() / duration), 0f)
            mGoogleMap?.addMarker(
                MarkerOptions()
                    .position(it.latLng)
                    .draggable(false)
                    .title(it.date + " " + it.time + "\n" + it.reason)
            )

            options.add(it.latLng)
            options.color(Color.parseColor("#9c27b0"))
        }
        mGoogleMap?.addPolyline(options)
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
        val handler = Handler()
        val start = SystemClock.uptimeMillis()
        val duration = 1500

        val interpolator = BounceInterpolator()

        handler.post(object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - start
                val t = Math.max(1 - interpolator.getInterpolation(elapsed.toFloat() / duration), 0f)
                marker?.setAnchor(0.5f, 1.0f + 0.5f * t)

                // Post again 16ms later.
                if (t > 0.0) {
                    handler.postDelayed(this, 8)
                }
            }
        })
        return false
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
