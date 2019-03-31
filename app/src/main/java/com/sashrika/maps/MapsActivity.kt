package com.sashrika.maps

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var lastMarker: Marker

    private lateinit var lastPosition: LatLng

    val TAG = "MainActivity"

    private val locationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private lateinit var locationCallback: LocationCallback

    private val locationRequest: LocationRequest by lazy {
        LocationRequest.create().apply {
            this.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            this.interval = 1000
            this.fastestInterval = 500
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        with(mMap.uiSettings) {
            setAllGesturesEnabled(true)
            isZoomControlsEnabled = true
            isCompassEnabled = true
        }

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)

        lastPosition = sydney

        lastMarker = mMap.addMarker(
                MarkerOptions()
                        .position(sydney)
                        .title("Marker in Sydney")
                        .draggable(true)
        )

//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 5f))
//
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 3f))

        mMap.setOnMapClickListener {

            lastMarker.remove()

            lastMarker = mMap.addMarker(
                    MarkerOptions()
                            .position(it)
                            .title("New Location")
                            .draggable(true)
            )

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 3f))

            locationCallback = object : LocationCallback() {

                override fun onLocationResult(locationResult: LocationResult?) {

                    Toast.makeText(baseContext, "LocationResult Called", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "loc ")

                    locationResult?.let {

                        val lastKnownLocation = it.lastLocation

                        val location: Location = it.locations[0]
                        var position = LatLng(location.latitude, location.longitude)

//                    Log.e(TAG, "Latitude is ${location.latitude}")
//                    Log.e(TAG, "Longitude is ${location.longitude}")
//                    Log.e(TAG, "Accuracy is ${location.accuracy}")
//                    Log.e(TAG, "Altitude is ${location.altitude}")
//                    Log.e(TAG, "Speed is ${location.speed}")
//                    Log.e(TAG, "Time in milliseconds is ${location.time}")
//                    Log.e(TAG, "Location provider is ${location.provider}")
                        mMap.addPolyline(
                                PolylineOptions()
                                        .add(lastPosition, position)
                                        .color(ContextCompat.getColor(baseContext, R.color.colorPrimary))
                                        .width(2f)
                                        .zIndex(2f)
                        )

                        Log.e(TAG, "loc ${lastPosition.latitude}")
//                    Toast.makeText(baseContext,"Location Shown", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
                    locationAvailability?.let {
                        if (it.isLocationAvailable)
                            Toast.makeText(baseContext, "Back online!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val locationSettingsRequest = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                    .build()


            val settingClient = LocationServices.getSettingsClient(this)

            settingClient.checkLocationSettings(locationSettingsRequest)
                    .addOnSuccessListener {

                        //Initialize a location request
                        locationClient.requestLocationUpdates(locationRequest, locationCallback, null)

                    }
                    .addOnFailureListener {
                        if (it is ResolvableApiException) {
                            it.startResolutionForResult(this, 12345)
                        }
                    }
        }
//        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
//
//            override fun onMarkerDragEnd(marker: Marker) {
//
//                Log.e("TAG", "Last marker : ${lastMarker.position.latitude} ${lastMarker.position.longitude}")
//                Log.e("TAG", "Current marker : ${marker.position.latitude} ${marker.position.longitude}")
////
//                mMap.addPolyline(
//                    PolylineOptions()
//                        .add(lastPosition, marker.position)
//                        .color(ContextCompat.getColor(baseContext, R.color.colorPrimary))
//                        .width(2f)
//                        .zIndex(2f)
//                )
//
////                mMap.addPolygon(
////                    PolygonOptions()
////                        .add(sydney, lastPosition, marker.position)
////                        .fillColor(ContextCompat.getColor(baseContext, R.color.colorAccent))
////                        .strokeColor(ContextCompat.getColor(baseContext, R.color.colorPrimary))
////                        .strokeWidth(2f)
////                        .zIndex(2f)
////                )
//
////                mMap.addCircle(
////                        CircleOptions()
////                                .radius(100.0)
////                                .center(lastPosition)
////                                .fillColor(ContextCompat.getColor(baseContext, R.color.colorAccent))
////                                .strokeColor(ContextCompat.getColor(baseContext, R.color.colorPrimary))
////                                .strokeWidth(2f)
////                )
//
//                lastPosition = marker.position
//
//                lastMarker.remove()
//
//                lastMarker = mMap.addMarker(
//                        MarkerOptions()
//                                .position(marker.position)
//                                .title("New Location")
//                                .draggable(true)
//                )
//
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 10f))
//            }
//
//            override fun onMarkerDragStart(marker: Marker) {
//
//                lastMarker = marker
//
//                Log.e("TAG", "Start marker : ${marker.position.latitude} ${marker.position.longitude}")
//            }
//
//            override fun onMarkerDrag(marker: Marker) {
//
//            }
//
//        })

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

//        val map = supportFragmentManager.findFragmentByTag("test") as SupportMapFragment

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

}

//collapsing toolbar -> antoniolieva