package com.gurkan.nearbyrestaurantapp.ui.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    fun signUp(fullName: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Save user's full name in Firebase
                    val user = auth.currentUser
                    user?.let {
                        val database = FirebaseDatabase.getInstance()
                        val databaseReference = database.reference.child("profile")
                        databaseReference.child(it.uid).child("fullName").setValue(fullName)
                    }

                }
            }
    }
}
