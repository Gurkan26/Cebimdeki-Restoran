package com.gurkan.nearbyrestaurantapp.ui.placeCommentList

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.gurkan.nearbyrestaurantapp.databinding.ActivityPlaceCommentBinding
import com.gurkan.nearbyrestaurantapp.model.Comment
import com.gurkan.nearbyrestaurantapp.ui.comment.commentList.CommentListAdapter
import com.gurkan.nearbyrestaurantapp.ui.details.specialPlaceName
import java.util.ArrayList

class PlaceCommentActivity : AppCompatActivity() {

    private lateinit var placeCommentRecyclerView: RecyclerView
    lateinit var placeCommentList: ArrayList<Comment>
    private lateinit var dbref: DatabaseReference
    lateinit var binding: ActivityPlaceCommentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        placeCommentRecyclerView = binding.rvPlaceComment
        placeCommentRecyclerView.layoutManager = LinearLayoutManager(this)

        placeCommentList = arrayListOf<Comment>()
        getCommentData()

    }

    private fun getCommentData() {

        dbref = FirebaseDatabase.getInstance().getReference("Comments")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())// var mı yok mu kontrol
                {
                    for (commentSnapshot in snapshot.children) {
                        val comment = commentSnapshot.getValue(Comment::class.java)
                        placeCommentList.add(comment!!)
                    }
                    placeCommentRecyclerView.adapter =
                        PlaceCommentAdapter(placeCommentList, specialPlaceName)
                } else {
                    Log.e("PlaceCommentActivity", "Hata")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}