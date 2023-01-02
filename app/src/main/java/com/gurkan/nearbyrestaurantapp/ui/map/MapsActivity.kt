package com.gurkan.nearbyrestaurantapp.ui.map

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gurkan.nearbyrestaurantapp.R
import com.gurkan.nearbyrestaurantapp.databinding.ActivityMapsBinding
import com.gurkan.nearbyrestaurantapp.model.Result
import com.gurkan.nearbyrestaurantapp.ui.comment.CommentActivity
import com.gurkan.nearbyrestaurantapp.ui.details.ViewPlace
import com.gurkan.nearbyrestaurantapp.ui.map.recyclerView.RecyclerViewAdapter
import com.gurkan.nearbyrestaurantapp.ui.map.recyclerView.placesClient
import com.gurkan.nearbyrestaurantapp.ui.profile.ProfileActivity
import java.io.ByteArrayOutputStream
import java.util.*


private lateinit var binding: ActivityMapsBinding
private lateinit var mMap: GoogleMap
private lateinit var mLastLocation: Location

@SuppressLint("StaticFieldLeak")
lateinit var fusedLocationProviderClient: FusedLocationProviderClient
private var mMarker: Marker? = null
private lateinit var rvAdapter: RecyclerViewAdapter
lateinit var placeList: ArrayList<Result>

//Location
private lateinit var locationRequest: LocationRequest
private lateinit var locationCallback: LocationCallback


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val viewModel: MapsViewModel by viewModels()

    companion object {
        const val MY_PERMISSION_CODE: Int = 1000
        const val MIN_DISTANCE = 150

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MapsInitializer.initialize(applicationContext)


        //PLACE INITIALIZE
        Places.initialize(this, getString(R.string.api_key))
        placesClient = Places.createClient(this)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)


        //Map Kullanma İzni
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {


                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                buildLocationRequest()
                buildLocationCallBack()

                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest, locationCallback, Looper.myLooper()
                )


            } else {

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

                buildLocationRequest()
                buildLocationCallBack()

                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest, locationCallback, Looper.myLooper()
                )


            }


        }
        initRecyclerView()
        viewModel.mapsResponse.observe(this, Observer {
            if (it != null) {
                placeList = it.results //filter için.
                //    fusedLocationProviderClient.removeLocationUpdates(locationCallback)   //Güncellemeyi durdurmak için
                rvAdapter.setPlacesList(placeList)
                rvAdapter.notifyDataSetChanged()

                rvAdapter.setOnClickListener(object : RecyclerViewAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {


                        val placeId = it.results[position].place_id

                        val destLocationLat = (it.results[position].geometry.location.lat)
                        val destLocationLong = (it.results[position].geometry.location.lng)
                        val lastLocationLat = mLastLocation.latitude
                        val lastLocationLong = mLastLocation.longitude

                        placeDetails(
                            placeId,
                            destLocationLat,
                            destLocationLong,
                            lastLocationLat,
                            lastLocationLong
                        )


                    }

                })


            }


        })

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.commentActivity -> {
                    startActivity(Intent(applicationContext, CommentActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.profileActivity -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.mapsActivity -> return@OnNavigationItemSelectedListener true


            }
            false
        })


    }


    fun placeDetails(
        placeId: String,
        destLocationLat: Double,
        destLocationLong: Double,
        lastLocationLat: Double,
        lastLocationLong: Double,
    ) {

        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.BUSINESS_STATUS,
            Place.Field.PHONE_NUMBER,
            Place.Field.RATING
        )
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(request).addOnSuccessListener { response: FetchPlaceResponse ->
            val place = response.place


            val intent = Intent(this@MapsActivity, ViewPlace::class.java)
            intent.putExtra("name", place.name)
            place.rating?.let { intent.putExtra("rating", it) }
            intent.putExtra("address", place.address)
            intent.putExtra("b_status", place.businessStatus.name)
            intent.putExtra("placeNumber", place.phoneNumber)
            intent.putExtra("placeId", placeId)
            intent.putExtra("destLocationLat", destLocationLat)
            intent.putExtra("destLocationLong", destLocationLong)
            intent.putExtra("lastLocationLat", lastLocationLat)
            intent.putExtra("lastLocationLong", lastLocationLong)
            startActivity(intent)

        }.addOnFailureListener { exception: Exception ->
            if (exception is ApiException) {
                val statusCode = exception.statusCode
                TODO("Handle error with given status code")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem: MenuItem = menu!!.findItem(R.id.actionSearch)
        val searchView: SearchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(text: String?): Boolean {
                if (text != null) {
                    filter(text)
                }
                return false
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.filterMenuRating3 -> {

                filterRating()
                true

            }
            R.id.filterMenuAlfabe -> {
                rvAdapter.filterList(filterAlf())
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun filterAlf(): ArrayList<Result> {

        return ArrayList(placeList.sortedBy { it.name })
    }

    private fun filterRating() {
        val filteredList: ArrayList<Result> = ArrayList()

        for (item in placeList) {
            if (item.rating >= 3.0) {
                filteredList.add(item)
            }
        }
        rvAdapter.filterList(ArrayList(filteredList.sortedBy { it.rating }))


    }


    private fun filter(text: String) {
        val filteredList: ArrayList<Result> = ArrayList()

        for (item in placeList) {
            if (item.name.toLowerCase().trim().contains(text.toLowerCase().trim())) {

                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(this, getString(R.string.mapsDataNotFound), Toast.LENGTH_SHORT).show()
        } else {
            rvAdapter.filterList(filteredList)
        }

    }

    private fun initRecyclerView() {
        binding.rvRestaurant.layoutManager = LinearLayoutManager(this)
        rvAdapter = RecyclerViewAdapter()
        binding.rvRestaurant.adapter = rvAdapter
        placeList = ArrayList<Result>()

    }


    private fun restaurantNearby(latitude: Double, longitude: Double) {
        // build Request
        val url =
            ("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=" + 1000 + "&type=restaurant" + "&key=" + resources.getString(
                R.string.api_key
            ))
        viewModel.makeApiCall(url)
    }


    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                mLastLocation = p0.locations[p0.locations.size - 1] // En son Lokasyon

                if (mMarker != null) {
                    mMarker!!.remove()
                }
                val latitude = mLastLocation.latitude
                val longitude = mLastLocation.longitude

                restaurantNearby(latitude, longitude)

                val latLng = LatLng(latitude, longitude)
                val markerOptions = MarkerOptions().position(latLng).title("Your Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                mMarker = mMap.addMarker(markerOptions)

                val cameraPosition =
                    CameraPosition.builder().target(latLng).zoom(17f).bearing(90f).tilt(40f).build()
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

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
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, android.Manifest.permission.ACCESS_FINE_LOCATION
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
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSION_CODE -> (if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(
                        this, android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    if (checkLocationPermission()) {
                        mMap.isMyLocationEnabled = true
                    }
                }
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            })
        }
    }


    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //Init Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_FINE_LOCATION
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

    override fun onDestroy() {
        super.onDestroy()
    }

}