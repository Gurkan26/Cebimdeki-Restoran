package com.gurkan.nearbyrestaurantapp.ui.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.gurkan.nearbyrestaurantapp.R
import com.gurkan.nearbyrestaurantapp.databinding.FragmentRegisterBinding

class FragmentRegister : Fragment() {
    private lateinit var viewModel: RegisterViewModel
    lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]


        binding.registerButton.setOnClickListener {
            val fullName = binding.tbFullName.text.toString()
            val email = binding.tbEmail.text.toString()
            val password = binding.tbPassword.text.toString()
            viewModel.signUp(fullName, email, password)
            findNavController().navigate(FragmentRegisterDirections.actionFragmentRegisterToFragmentLogin())
        }



        binding.tbSignInNow.setOnClickListener {
            findNavController().navigate(FragmentRegisterDirections.actionFragmentRegisterToFragmentLogin())
        }

        binding.tbForgotPass.setOnClickListener {
            findNavController().navigate(FragmentRegisterDirections.actionFragmentRegisterToFragmentForgotPassword())
        }

        return binding.root
    }


}