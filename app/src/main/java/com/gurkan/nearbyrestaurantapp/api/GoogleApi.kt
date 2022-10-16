package com.gurkan.nearbyrestaurantapp.api

import com.gurkan.nearbyrestaurantapp.Model.MyPlaces
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface GoogleApi {

    @GET("")
    fun getNearbyRestaurant(@Url url:String): Call<MyPlaces>

}