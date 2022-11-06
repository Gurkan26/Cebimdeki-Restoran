package com.gurkan.nearbyrestaurantapp.model

import com.google.gson.annotations.SerializedName


data class MyPlaces(
    val results: ArrayList<Result>,
    @SerializedName("next_page_token")
    val nextPageToken: String
)

data class Result(
    val business_status: String,
    val name: String,
    val photos: ArrayList<Photo>,
    val place_id: String,
    val rating: Double,
    val reference: String,
    val geometry: Geometry,
    val vicinity: String
) {

    data class Geometry(
        @SerializedName("location")
        val location: Location,
        ) {
        data class Location(
            @SerializedName("lat")
            val lat: Double,
            @SerializedName("lng")
            val lng: Double
        )
    }
}

data class Photo(
    @SerializedName("height")
    val height: Int,
    @SerializedName("html_attributions")
    val htmlAttributions: List<String>,
    @SerializedName("photo_reference")
    val photoReference: String,
    @SerializedName("width")
    val width: Int
)






