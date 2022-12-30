package com.gurkan.nearbyrestaurantapp.ui.details

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.gurkan.nearbyrestaurantapp.ui.profile.ProfileActivity
import com.gurkan.nearbyrestaurantapp.ui.comment.CommentActivity
import com.gurkan.nearbyrestaurantapp.R
import com.gurkan.nearbyrestaurantapp.databinding.ActivityViewPlaceBinding
import com.gurkan.nearbyrestaurantapp.model.GoogleMapDTO
import com.gurkan.nearbyrestaurantapp.ui.map.MapsActivity
import com.gurkan.nearbyrestaurantapp.ui.placeCommentList.PlaceCommentActivity
import okhttp3.OkHttpClient
import okhttp3.Request


var specialPlaceName = ""

class ViewPlace : AppCompatActivity(),
    OnMapReadyCallback {

    private lateinit var auth: FirebaseAuth
    var databaseReference: DatabaseReference? = null
    var database: FirebaseDatabase? = null
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

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapDirection) as SupportMapFragment
        mapFragment.getMapAsync(this)
        var ratingBarSet: Double? = 0.0

        val rating: RatingBar = findViewById(R.id.ratingBar)


        val bundle: Bundle? = intent.extras
        binding.tbName.text = bundle!!.getString("name")
        specialPlaceName = binding.tbName.text.toString()
        ratingBarSet = bundle.getDouble("rating")
        binding.tbAddress.text = bundle.getString("address")
        var phoneNumber = bundle.getString("placeNumber")
        destLocLat = bundle.getDouble("destLocationLat")
        destLocLng = bundle.getDouble("destLocationLong")
        lastLocLat = bundle.getDouble("lastLocationLat")
        lastLocLng = bundle.getDouble("lastLocationLong")
        var placeId = bundle.getString("placeId")
        // imageBitmap = bundle.getByteArray("imageBitmap")
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


        // Kullanıcı id'si ile kullanıcının adını çekme
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("profile")

        var currentUser = auth.currentUser
        var userName = ""

        var userReference = databaseReference?.child(currentUser?.uid!!)
        userReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userName = snapshot.child("fullName").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        // yorum gönderme
        binding.btnDegerlendir.setOnClickListener {


            var etComment = binding.etComment.text.toString()
            var placeName = binding.tbName.text.toString()
            if (TextUtils.isEmpty(etComment)) {
                binding.etComment.error = getString(R.string.viewPlaceNotEmpty)
            } else {
                var database = FirebaseDatabase.getInstance()
                var databaseReference = database.reference.child("Comments")
                var id = databaseReference.push()
                id.child("id").setValue(id.key.toString())
                id.child("placeName").setValue(placeName)
                id.child("placeComment").setValue(etComment)
                id.child("placeRating").setValue(ratingBarSet.toFloat())
                id.child("userName").setValue(userName)
                id.child("placeId").setValue(placeId)

                Toast.makeText(
                    applicationContext,
                    getString(R.string.viewPlaceCommentSend),
                    Toast.LENGTH_LONG
                ).show()
            }


        }

        // navigate sayfa taşıma
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
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

                    R.id.mapsActivity -> {
                        startActivity(Intent(applicationContext, MapsActivity::class.java))
                        overridePendingTransition(0, 0)
                        return@OnNavigationItemSelectedListener true
                    }


                }
                false
            })

        binding.btnPlaceComments.setOnClickListener {

            startActivity(Intent(applicationContext, PlaceCommentActivity::class.java))
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

    private inner class GetDirection(val url: String) : //POLY LİNE
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