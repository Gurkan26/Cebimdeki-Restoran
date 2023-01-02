package com.gurkan.nearbyrestaurantapp.ui.login

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class LoginRepository(private val auth: FirebaseAuth) {
    val loginResult = MutableLiveData<Boolean>()

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loginResult.value = task.isSuccessful
            }
    }
}
