package com.gurkan.nearbyrestaurantapp.ui.map

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.gurkan.nearbyrestaurantapp.model.MyPlaces
import com.gurkan.nearbyrestaurantapp.api.RetrofitClient
import com.gurkan.nearbyrestaurantapp.model.Result
import com.gurkan.nearbyrestaurantapp.ui.recyclerView.RecyclerViewAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapsViewModel : ViewModel() {
    private var _mapsResponse: MutableLiveData<MyPlaces> = MutableLiveData()
    val mapsResponse: LiveData<MyPlaces> = _mapsResponse


    fun makeApiCall(url: String) {
        RetrofitClient.retrofitApi.getNearbyRestaurant(url)
            .enqueue(object : Callback<MyPlaces> {
                override fun onResponse(call: Call<MyPlaces>, response: Response<MyPlaces>) {
                    if (response!!.isSuccessful) {


                        _mapsResponse.postValue(response.body())


                    }

                }

                override fun onFailure(call: Call<MyPlaces>, t: Throwable) {
                    Toast.makeText(null, "" + t.message, Toast.LENGTH_SHORT).show()
                }

            })
    }


}
