package com.gurkan.nearbyrestaurantapp.ui.profile.logout

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.gurkan.nearbyrestaurantapp.MainActivity
import com.gurkan.nearbyrestaurantapp.R
import com.gurkan.nearbyrestaurantapp.databinding.FragmentProfilLogoutBinding
import com.gurkan.nearbyrestaurantapp.model.firebase.ProfileModel
import com.gurkan.nearbyrestaurantapp.ui.profile.ProfileViewModel
import com.gurkan.nearbyrestaurantapp.ui.profile.ProfileViewModelFactory

var specialuserName = ""

class FragmentProfilLogout : Fragment() {

    private lateinit var binding: FragmentProfilLogoutBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfilLogoutBinding.inflate(inflater, container, false)

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        var userEmail= FirebaseAuth.getInstance()

        val profileModel = ProfileModel()
        val profileViewModel: ProfileViewModel by viewModels {
            ProfileViewModelFactory(profileModel)
        }
        profileViewModel.getUser(userId)
        profileViewModel.user.observe(viewLifecycleOwner, Observer { user ->
            binding.tbMail.text = userEmail.currentUser?.email
            binding.tbFullName.text = user.fullName
            specialuserName = user.fullName
        })

        binding.btnExit.setOnClickListener {
            1
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireActivity(), MainActivity::class.java))
        }

        binding.btnUpdate.setOnClickListener {
            findNavController().navigate(FragmentProfilLogoutDirections.actionFragmentProfilLogoutToFragmentUpdate())
        }
        binding.btnUserComments.setOnClickListener {
            findNavController().navigate(FragmentProfilLogoutDirections.actionFragmentProfilLogoutToFragmentUserComment())
        }

        return binding.root
    }
}



