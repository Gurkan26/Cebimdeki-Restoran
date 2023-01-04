package com.gurkan.nearbyrestaurantapp.ui.profile.logout

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.gurkan.nearbyrestaurantapp.MainActivity
import com.gurkan.nearbyrestaurantapp.databinding.FragmentProfilLogoutBinding
import com.gurkan.nearbyrestaurantapp.ui.profile.ProfileViewModel

class FragmentProfilLogout : Fragment() {

    private lateinit var binding: FragmentProfilLogoutBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfilLogoutBinding.inflate(inflater, container, false)

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        viewModel.getUser(userId)
        viewModel.user.observe(viewLifecycleOwner, Observer { user ->
            binding.tbMail.text = "Email: ${user.email}"
            binding.tbFullName.text = "Adınız: ${user.fullName}"
        })

        binding.btnExit.setOnClickListener {
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