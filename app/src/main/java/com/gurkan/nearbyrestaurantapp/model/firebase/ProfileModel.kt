package com.gurkan.nearbyrestaurantapp.model.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class User {
    var email: String = ""
    var fullName: String = ""
}

class ProfileModel {
    fun getUser(userId: String, onSuccess: (User) -> Unit, onFailure: () -> Unit) {
        val databaseReference =
            FirebaseDatabase.getInstance().reference!!.child("profile").child(userId)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    onSuccess(user)
                } else {
                    onFailure()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure()
            }
        })
    }

    fun updateEmail(email: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.updateEmail(email)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                onFailure()
            }
        }
    }


    fun updatePassword(password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.updatePassword(password)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                onFailure()
            }
        }
    }


    fun updateFullName(fullName: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference =
            FirebaseDatabase.getInstance().reference!!.child("profile").child(userId)
        databaseReference.child("fullName").setValue(fullName)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure()
                }
            }
    }
}
