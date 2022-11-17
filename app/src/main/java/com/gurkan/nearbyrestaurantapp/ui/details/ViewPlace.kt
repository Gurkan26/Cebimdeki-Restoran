package com.gurkan.nearbyrestaurantapp.ui.details

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ImageView
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.gurkan.nearbyrestaurantapp.R
import com.gurkan.nearbyrestaurantapp.databinding.ActivityViewPlaceBinding
import com.gurkan.nearbyrestaurantapp.model.GoogleMapDTO
import com.gurkan.nearbyrestaurantapp.ui.map.*
import okhttp3.OkHttpClient
import okhttp3.Request

class ViewPlace : AppCompatActivity(),
    OnMapReadyCallback {
    private lateinit var binding: ActivityViewPlaceBinding
    lateinit var mMap: GoogleMap

    var destLocLat = 0.0
    var destLocLng = 0.0
    var lastLocLat = 0.0
    var lastLocLng = 0.0
    var destLatLng: LatLng? = null
    var lastLatLng: LatLng? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapDirection) as SupportMapFragment
        mapFragment.getMapAsync(this)
        var ratingBarSet: Double? = 0.0

        val rating: RatingBar = findViewById(R.id.ratingBar)
        val status: ImageView = findViewById(R.id.statusImg)
        var statusValue = ""


        val bundle: Bundle? = intent.extras
        binding.tbName.text = bundle!!.getString("name")
        ratingBarSet = bundle.getDouble("rating")
        statusValue = bundle.getString("b_status").toString()
        binding.tbAddress.text = bundle.getString("address")
        var phoneNumber = bundle.getString("placeNumber")
        destLocLat = bundle.getDouble("destLocationLat")
        destLocLng = bundle.getDouble("destLocationLong")
        lastLocLat = bundle.getDouble("lastLocationLat")
        lastLocLng = bundle.getDouble("lastLocationLong")
        rating.rating = ratingBarSet.toFloat()
        destLatLng = LatLng(destLocLat, destLocLng)
        lastLatLng = LatLng(lastLocLat, lastLocLng)


        mapFragment.getMapAsync {
            mMap = it
            val originLocation = LatLng(lastLocLat, lastLocLng)
            mMap.addMarker(
                MarkerOptions().position(originLocation).icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                )
            )
            val destinationLocation = LatLng(destLocLat, destLocLng)
            mMap.addMarker(MarkerOptions().position(destinationLocation))
            val url = getDirectionURL(originLocation, destinationLocation)
            GetDirection(url).execute()
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation, 14F))

        }


        // Arama Kısmı
        binding.callBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(intent)
        }

        statusControl(statusValue, status) // Dükkan durum kontrol


    }


    private fun statusControl(statusValue: String, status: ImageView) {
        if (statusValue == "CLOSED_TEMPORARILY") {
            Glide.with(this).load(R.drawable.closed).into(status)

        } else {
            Glide.with(this).load(R.drawable.open).into(status)


        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        val options = PolylineOptions()
        options.color(Color.BLUE)
        options.width(11f)



        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng!!, 14.5f))


    }


    private fun getDirectionURL(origin: LatLng, dest: LatLng): String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=walking" +
                "&key=AIzaSyBQ2tXz0E6sUrRUrZtzhBZiF4dmspgMFW0"
    }

    private inner class GetDirection(val url: String) :
        AsyncTask<Void, Void, List<List<LatLng>>>() {
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {

            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()

            val result = ArrayList<List<LatLng>>()
            try {
                val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)
                val path = ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices) {
                lineoption.addAll(result[i])
                lineoption.width(7f)
                lineoption.color(Color.BLUE)

                lineoption.geodesic(true)
            }
            mMap.addPolyline(lineoption)
        }
    }


    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }
}