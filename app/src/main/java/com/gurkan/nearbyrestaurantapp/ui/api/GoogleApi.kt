package com.gurkan.nearbyrestaurantapp.ui.api

import com.gurkan.nearbyrestaurantapp.model.MyPlaces
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface GoogleApi {

    @GET
    fun getNearbyRestaurant(@Url url: String): Call<MyPlaces>

}