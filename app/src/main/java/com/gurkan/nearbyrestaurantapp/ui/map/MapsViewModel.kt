package com.gurkan.nearbyrestaurantapp.ui.map

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.gurkan.nearbyrestaurantapp.Model.MyPlaces
import com.gurkan.nearbyrestaurantapp.R
import com.gurkan.nearbyrestaurantapp.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.StringBuilder
import kotlin.coroutines.coroutineContext

private lateinit var mMap: GoogleMap

class MapsViewModel : ViewModel() {
    private val _mapsResponse: MutableLiveData<MyPlaces> = MutableLiveData()
    val mapsResponse: LiveData<MyPlaces> = _mapsResponse

    fun makeApiCall(url: String, latitude:Double,longitude:Double) {
        RetrofitClient.retrofitApi.getNearbyRestaurant(url)
            .enqueue(object : Callback<MyPlaces> {
                override fun onResponse(call: Call<MyPlaces>, response: Response<MyPlaces>) {
                    if (response.isSuccessful) {

                        for (i in 0 until response.body()!!.results.size) {
                            val markerOptions = MarkerOptions()
                            val googlePlace = response.body()!!.results[i]
                            val lat = googlePlace.geometry.location.lat
                            val lng = googlePlace.geometry.location.lng
                            val placeName = googlePlace.name
                            val latLng = LatLng(lat, lng)
                            markerOptions.position(latLng)
                            markerOptions.title(placeName)

                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_restaurant_menu_24))


                            markerOptions.snippet(i.toString())

                            mMap.addMarker(markerOptions)


                        }
                        //Move Camera
                        mMap.moveCamera(
                            CameraUpdateFactory.newLatLng(
                                LatLng(
                                    latitude,
                                    longitude
                                )
                            )
                        )
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))

                    }

                }

                override fun onFailure(call: Call<MyPlaces>, t: Throwable) {
                    Toast.makeText(null, "" + t.message, Toast.LENGTH_SHORT).show()
                }

            })
    }



}