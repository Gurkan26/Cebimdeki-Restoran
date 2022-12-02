package com.gurkan.nearbyrestaurantapp.ui.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val api: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }
    val retrofitApi: GoogleApi by lazy {
        api.create(GoogleApi::class.java)
    }
}