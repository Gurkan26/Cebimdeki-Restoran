package com.gurkan.nearbyrestaurantapp.ui.placeCommentList

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gurkan.nearbyrestaurantapp.model.Comment

class PlaceCommentViewModel : ViewModel() {
    val placeCommentList = MutableLiveData<ArrayList<Comment>>()

    fun getCommentData() {
        val dbref = FirebaseDatabase.getInstance().getReference("Comments")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val comments = arrayListOf<Comment>()
                    for (commentSnapshot in snapshot.children) {
                        val comment = commentSnapshot.getValue(Comment::class.java)
                        comments.add(comment!!)
                    }
                    placeCommentList.value = comments
                } else {
                    Log.e("PlaceCommentActivity", "Error")
                }
            }

            override fun onCancelled(error: DatabaseError) { }
        })
    }
}