package com.gurkan.nearbyrestaurantapp.ui.map.recyclerView


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import com.gurkan.nearbyrestaurantapp.R
import com.gurkan.nearbyrestaurantapp.databinding.ItemsBinding
import com.gurkan.nearbyrestaurantapp.model.Result


lateinit var placesClient: PlacesClient

class RecyclerViewAdapter :
    RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {
    private var items = ArrayList<Result>()


    private lateinit var mListener: onItemClickListener

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filter: ArrayList<Result>) {
        items = filter
        notifyDataSetChanged()

    }

    fun setPlacesList(item: ArrayList<Result>) {

        this.items = item
    }


    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnClickListener(listener: onItemClickListener) {
        mListener = listener


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,

                false
            ), mListener
        )

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        holder.bind(items[position])


    }


    override fun getItemCount() = items.size

    class MyViewHolder(
        private val binding: ItemsBinding,
        listener: onItemClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        // private val rnd = Random(1000)
        fun bind(data: Result) {

            binding.place = data
            binding.ratingTextView.text = data.rating.toString()
            getPhoto(data.place_id, binding.thubmImage)
            if (data.business_status == "CLOSED_TEMPORARILY") {
                binding.placeControl.setImageResource(R.drawable.closepng)
            } else {
                binding.placeControl.setImageResource(R.drawable.openpng)
            }
            /*    val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                binding.cardView.setBackgroundColor(color)*/
            binding.executePendingBindings()


        }


        init {

            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)


            }

        }


    }


}

private fun getPhoto(placeId: String, imageView: ImageView) {

// Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
    val fields = listOf(Place.Field.PHOTO_METADATAS)

// Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
    val placeRequest = FetchPlaceRequest.newInstance(placeId, fields)

    placesClient?.fetchPlace(placeRequest)
        ?.addOnSuccessListener { response: FetchPlaceResponse ->
            val place = response.place

            // Get the photo metadata.
            val metada = place.photoMetadatas
            if (metada == null || metada.isEmpty()) {
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
                return@addOnSuccessListener
            }
            val photoMetadata = metada.first()

            // Get the attribution text.
            val attributions = photoMetadata?.attributions

            // Create a FetchPhotoRequest.
            val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(500) // Optional.
                .setMaxHeight(300) // Optional.
                .build()
            placesClient.fetchPhoto(photoRequest)
                .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                    val bitmap = fetchPhotoResponse.bitmap
                    imageView.setImageBitmap(bitmap)
                }.addOnFailureListener { exception: Exception ->
                    if (exception is ApiException) {

                        val statusCode = exception.statusCode
                        TODO("Handle error with given status code.")
                    }
                }
        }


}








