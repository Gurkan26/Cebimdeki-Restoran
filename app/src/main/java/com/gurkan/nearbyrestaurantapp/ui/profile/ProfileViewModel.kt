package com.gurkan.nearbyrestaurantapp.ui.profile

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gurkan.nearbyrestaurantapp.R
import com.gurkan.nearbyrestaurantapp.model.firebase.ProfileModel
import com.gurkan.nearbyrestaurantapp.model.firebase.User

class ProfileViewModel(private val profileModel: ProfileModel) :
    ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun getUser(userId: String) {
        profileModel.getUser(userId,
            onSuccess = { user ->
                _user.value = user
            },
            onFailure = {
                // Kullanıcı bilgileri çekilirken hata oluştu. Hata işleme yap.
            }
        )
    }

    fun updateEmail(email: String, context: Context) {
        profileModel.updateEmail(email,
            onSuccess = {
                Toast.makeText(context, R.string.updateEmailSucc, Toast.LENGTH_LONG).show()
            },
            onFailure = {
                Toast.makeText(context, R.string.updateEmailError, Toast.LENGTH_LONG).show()
            }
        )
    }

     fun updatePassword(password: String, context: Context) {
        profileModel.updatePassword(password,
            onSuccess = {
                Toast.makeText(context, R.string.updatePasswordSucc, Toast.LENGTH_LONG).show()
            },
            onFailure = {
                Toast.makeText(context, R.string.updatePasswordError, Toast.LENGTH_LONG).show()
            }
        )
    }

    fun updateFullName(fullName: String, context: Context) {
        profileModel.updateFullName(fullName,
            onSuccess = {
                Toast.makeText(context, R.string.updateFullNameSuccess, Toast.LENGTH_LONG).show()
            },
            onFailure = {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }
        )
    }
}

class ProfileViewModelFactory(private val profileModel: ProfileModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(profileModel) as T
    }
}
