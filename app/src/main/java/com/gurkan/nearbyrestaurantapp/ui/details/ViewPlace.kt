package com.gurkan.nearbyrestaurantapp.ui.details

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.gurkan.nearbyrestaurantapp.R
import com.gurkan.nearbyrestaurantapp.model.GoogleMapDTO
import com.gurkan.nearbyrestaurantapp.ui.map.*
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.math.abs

class ViewPlace : AppCompatActivity(), GestureDetector.OnGestureListener,
    OnMapReadyCallback {
    lateinit var mapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap
    private val viewModel: MapsViewModel by viewModels()

    var destLocLat = 0.0
    var destLocLng = 0.0
    var lastLocLat = 0.0
    var lastLocLng = 0.0
    var destLatLng: LatLng? = null
    var lastLatLng: LatLng? = null
    private lateinit var gestureDetector: GestureDetector
    var x2: Float = 0.0f
    var x1: Float = 0.0f
    var y2: Float = 0.0f
    var y1: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_place)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapDirection) as SupportMapFragment
        mapFragment.getMapAsync(this)
        var ratingBarSet: Double? = 0.0
        val name: TextView = findViewById(R.id.tbName)
        val rating: RatingBar = findViewById(R.id.ratingBar)
        val status: ImageView = findViewById(R.id.statusImg)
        var statusValue = ""
        val address: TextView = findViewById(R.id.tbAddress)


        val bundle: Bundle? = intent.extras
        name.text = bundle!!.getString("name")
        ratingBarSet = bundle.getDouble("rating")
        statusValue = bundle.getString("b_status").toString()
        address.text = bundle.getString("address")
        destLocLat = bundle.getDouble("destLocationLat")
        destLocLng = bundle.getDouble("destLocationLong")
        lastLocLat = bundle.getDouble("lastLocationLat")
        lastLocLng = bundle.getDouble("lastLocationLong")
        rating.rating = ratingBarSet.toFloat()
        destLatLng = LatLng(destLocLat, destLocLng)
        lastLatLng = LatLng(lastLocLat, lastLocLng)
        //      viewModel.makeApiCall(getDirectionURL(lastLatLng!!, destLatLng!!))

        val URL = getDirectionURL(lastLatLng!!, destLatLng!!)
        Log.d("GoogleMap", "URL : $URL")
        GetDirection(URL).execute()
        if (statusValue == "CLOSED_TEMPORARILY") {
            Glide.with(this).load(R.drawable.closed).into(status)

        } else {
            Glide.with(this).load(R.drawable.open).into(status)


        }

        gestureDetector = GestureDetector(this, this)


    }

    private fun getDirectionURL(origin: LatLng, dest: LatLng): String {
        return "https://maps.googleapis.com/maps/api/directions/json?&origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&key=${R.string.api_key}"
    }

    private inner class GetDirection(val url: String) :
        AsyncTask<Void, Void, List<List<LatLng>>>() {
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()
            Log.d("GoogleMap", " data : $data")
            val result = ArrayList<List<LatLng>>()
            try {
                val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)

                val path = ArrayList<LatLng>()

                for (i in 0..(respObj.routes[0].legs[0].steps.size - 1)) {
//                    val startLatLng = LatLng(respObj.routes[0].legs[0].steps[i].start_location.lat.toDouble()
//                            ,respObj.routes[0].legs[0].steps[i].start_location.lng.toDouble())
//                    path.add(startLatLng)
//                    val endLatLng = LatLng(respObj.routes[0].legs[0].steps[i].end_location.lat.toDouble()
//                            ,respObj.routes[0].legs[0].steps[i].end_location.lng.toDouble())
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
                lineoption.width(10f)
                lineoption.color(Color.BLUE)
                lineoption.geodesic(true)
            }
            googleMap.addPolyline(lineoption)
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
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
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


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event!!)
        when (event.action) {

            0 -> {
                x1 = event.x
                y1 = event.y
            }
            1 -> {
                x2 = event.x
                y2 = event.y

                val valueX: Float = x2 - x1
                val valueY: Float = y2 - y1
                if (abs(valueX) > MapsActivity.MIN_DISTANCE) {
                    if (x2 > x1) {
                        val intent = Intent(this, MapsActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }


    override fun onDown(p0: MotionEvent): Boolean {
        //TODO("Not yet implemented")
        return false
    }

    override fun onShowPress(p0: MotionEvent) {
        TODO("Not yet implemented")

    }

    override fun onSingleTapUp(p0: MotionEvent): Boolean {
        // TODO("Not yet implemented")
        return false

    }

    override fun onScroll(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean {
        //TODO("Not yet implemented")
        return false

    }

    override fun onLongPress(p0: MotionEvent) {
        // TODO("Not yet implemented")

    }

    override fun onFling(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean {
        // TODO("Not yet implemented")
        return false

    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap

        this.googleMap!!.addMarker(MarkerOptions().position(lastLatLng!!).title("Your Location"))
        this.googleMap!!.addMarker(MarkerOptions().position(destLatLng!!).title("Restaurant Location"))
        this.googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng!!, 14.5f))


    }
}