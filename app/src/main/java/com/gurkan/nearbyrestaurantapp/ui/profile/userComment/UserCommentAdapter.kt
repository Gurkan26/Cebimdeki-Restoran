package com.gurkan.nearbyrestaurantapp.ui.profile.userComment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.gurkan.nearbyrestaurantapp.R
import com.gurkan.nearbyrestaurantapp.model.Comment

var userCList = ArrayList<Comment>()

class UserCommentAdapter(
    private var userCommentList: ArrayList<Comment>,
    private var userName: String
) :
    RecyclerView.Adapter<UserCommentAdapter.MyViewHolder>() {
    init {
        commentFix()
    }

    class MyViewHolder(
        view: View,
    ) :
        RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.findViewById(R.id.placeNameComment)
        val placeComment: TextView = view.findViewById(R.id.placeComment)
        val userName: TextView = view.findViewById(R.id.tbUser)

        // val placeRating: RatingBar = view.findViewById(R.id.ratingBar)
        // val placeImage:ImageView=view.findViewById(R.id.thubmImage)


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.comment_items, parent, false
            )

        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.placeName.text = userCList[position].placeName
        holder.placeComment.text = userCList[position].placeComment
        holder.userName.text = userCList[position].userName


        // Glide.with(context).load(commentList[position].placeImage).into(holder.placeImage)
        // holder.placeRating.rating=commentList[position].placeRating.toFloat()

    }


    override fun getItemCount() = userCList.size

    private fun commentFix() {
        for (i in userCommentList) {
            if (i.userName == userName) {
                userCList.add(i)
            }
        }
    }
}

