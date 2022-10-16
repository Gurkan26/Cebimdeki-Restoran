package com.gurkan.nearbyrestaurantapp.ui.map

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import com.gurkan.nearbyrestaurantapp.R
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

import com.gurkan.nearbyrestaurantapp.databinding.ActivityMapsBinding

import java.lang.StringBuilder

private lateinit var binding: ActivityMapsBinding
private lateinit var mMap: GoogleMap
private lateinit var mLastLocation: Location
@SuppressLint("StaticFieldLeak")
private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private var latitude: Double = 0.toDouble()  // Enlem
    private var longitude: Double = 0.toDouble() // Boylam
    private var mMarker: Marker? = null

    //Location
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    val viewModel: MapsViewModel by viewModels()

    companion object {
        private const val MY_PERMISSION_CODE: Int = 1000

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Map Kullanma İzni
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )


            }
        } else {
            buildLocationRequest()
            buildLocationCallBack()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        }


    }


    private fun restaurantNearby() {
        //Clear Map
        mMap.clear()
        // build Request
        val type = "restaurant"
        val url = ("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                +latitude+","+longitude+
                "&radius="+10000+"&type="+type+"&key="+resources.getString(R.string.api_key))

        viewModel.makeApiCall(url, latitude, longitude)

    }

   /* private fun getUrl(latitude: Double, longitude: Double, type: String): String {
        val googlePlaceUrl =
            StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
        googlePlaceUrl.append("?location=$latitude,$longitude")
        googlePlaceUrl.append("&radius=5000") // 5km
        googlePlaceUrl.append("&type=${type}")
        googlePlaceUrl.append("&keyword=cruise")
        googlePlaceUrl.append("&key=AIzaSyBQ2tXz0E6sUrRUrZtzhBZiF4dmspgMFW0")

        Log.d("URL_DEBUG", googlePlaceUrl.toString())

        return googlePlaceUrl.toString()

    }*/

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                mLastLocation = p0.locations[p0.locations.size - 1] // En son Lokasyon

                if (mMarker != null) {
                    mMarker!!.remove()
                }
                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude

                val latLng = LatLng(latitude, longitude)
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("Your Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                mMarker = mMap.addMarker(markerOptions)

                // Move Map
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))
            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.smallestDisplacement = 10f
    }

    private fun checkLocationPermission(): Boolean {

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), MY_PERMISSION_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), MY_PERMISSION_CODE
                )

            }
            return false
        } else {
            return true
        }

    }

    @SuppressLint("MissingSuperCall")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSION_CODE -> (
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            if (checkLocationPermission()) {
                                mMap.isMyLocationEnabled = true
                            }
                        }
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                    }
                    )
        }
    }


    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        viewModel.mapsResponse.observe(this, Observer {
            if (it != null) {
                restaurantNearby()
            }

        })
        //Init Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mMap.isMyLocationEnabled = true
            }
        } else {
            mMap.isMyLocationEnabled = true
        }

        // Zoom Control
        mMap.uiSettings.isZoomControlsEnabled = true
    }
}