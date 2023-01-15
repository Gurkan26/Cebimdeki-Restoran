package com.gurkan.nearbyrestaurantapp.ui.profile.update

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.gurkan.nearbyrestaurantapp.R
import com.gurkan.nearbyrestaurantapp.databinding.FragmentUpdateBinding
import com.gurkan.nearbyrestaurantapp.model.firebase.ProfileModel
import com.gurkan.nearbyrestaurantapp.ui.profile.ProfileViewModel
import com.gurkan.nearbyrestaurantapp.ui.profile.ProfileViewModelFactory

class FragmentUpdate : Fragment() {

    private lateinit var binding: FragmentUpdateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateBinding.inflate(inflater, container, false)

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userMail = FirebaseAuth.getInstance()
        val profileModel = ProfileModel()
        val profileViewModel: ProfileViewModel by viewModels {
            ProfileViewModelFactory(profileModel)
        }
        profileViewModel.getUser(userId)
        profileViewModel.user.observe(viewLifecycleOwner, Observer { user ->
            binding.tbUpdateEmail.setText(userMail.currentUser?.email)
            binding.tbUpdateFullName.setText(user.fullName)
        })

        binding.buttonUpdate.setOnClickListener {
            val email = binding.tbUpdateEmail.text.toString().trim()
            profileViewModel.updateEmail(email, requireContext())

            val password = binding.tbUpdatePassword.text.toString().trim()
            if (password.isNotEmpty()) {
                profileViewModel.updatePassword(password, requireContext())
            }

            val fullName = binding.tbUpdateFullName.text.toString().trim()
            profileViewModel.updateFullName(fullName, requireContext())
        }

        return binding.root
    }
}
