package com.gurkan.nearbyrestaurantapp.ui.forgotPassword

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.gurkan.nearbyrestaurantapp.R
import android.content.res.Resources
import android.provider.Settings.System.getString
import androidx.core.content.ContentProviderCompat.requireContext

class ForgotPasswordViewModel(private val context: ContentResolver) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    val email = MutableLiveData<String>()
    private val successMessage = MutableLiveData<String>()
    private val errorMessage = MutableLiveData<String>()

    @SuppressLint("RestrictedApi")
    fun sendPasswordResetEmail() {
        auth.sendPasswordResetEmail(email.value ?: "")
            .addOnCompleteListener { resetMail ->
                if (resetMail.isSuccessful) {
                    successMessage.value = getString(
                        context,
                        R.string.forgotPassResetLink.toString()
                    )
                } else {
                    errorMessage.value = getString(
                        context,
                        R.string.forgotPassSendFailed.toString()
                    )
                }
            }
    }
}
