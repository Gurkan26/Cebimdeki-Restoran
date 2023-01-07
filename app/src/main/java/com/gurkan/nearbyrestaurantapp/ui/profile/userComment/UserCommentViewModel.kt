package com.gurkan.nearbyrestaurantapp.ui.profile.userComment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gurkan.nearbyrestaurantapp.model.Comment

class UserCommentViewModel : ViewModel() {
    val comments = MutableLiveData<List<Comment>>()

    fun getCommentData() {
        val dbref = FirebaseDatabase.getInstance().getReference("Comments")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val commentList = mutableListOf<Comment>()
                    for (commentSnapshot in snapshot.children) {
                        val comment = commentSnapshot.getValue(Comment::class.java)
                        commentList.add(comment!!)
                    }
                    comments.value = commentList
                } else {
                    Log.e("FragmentUserComment", "Hata")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}