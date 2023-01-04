package com.gurkan.nearbyrestaurantapp.ui.profile.update

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.gurkan.nearbyrestaurantapp.R
import com.gurkan.nearbyrestaurantapp.databinding.FragmentUpdateBinding
import com.gurkan.nearbyrestaurantapp.ui.profile.ProfileViewModel

class FragmentUpdate : Fragment() {

    private lateinit var binding: FragmentUpdateBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateBinding.inflate(inflater, container, false)

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        viewModel.getUser(userId)
        viewModel.user.observe(viewLifecycleOwner, Observer { user ->
            binding.tbUpdateEmail.setText(user.email)
            binding.tbUpdateFullName.setText(user.fullName)
        })

        binding.buttonUpdate.setOnClickListener {
            val email = binding.tbUpdateEmail.text.toString().trim()
            viewModel.updateEmail(email, requireContext())

            val password = binding.tbUpdatePassword.text.toString().trim()
            if (password.isNotEmpty()) {
                viewModel.updatePassword(password, requireContext())
            }

            val fullName = binding.tbUpdateFullName.text.toString().trim()
            viewModel.updateFullName(fullName, requireContext())
        }

        return binding.root
    }
}
