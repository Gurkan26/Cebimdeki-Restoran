package com.gurkan.nearbyrestaurantapp.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LoginRepository
    val loginResult: MutableLiveData<Boolean>

    init {
        val auth = FirebaseAuth.getInstance()
        repository = LoginRepository(auth)
        loginResult = repository.loginResult as MutableLiveData<Boolean>
    }

    fun login(email: String, password: String) {
        repository.login(email, password)
    }
}