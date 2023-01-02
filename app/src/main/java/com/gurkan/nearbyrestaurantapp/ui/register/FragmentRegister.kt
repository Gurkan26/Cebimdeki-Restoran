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

        viewModel.fullName.observe(viewLifecycleOwner, Observer {
            binding.tbFullName.setText(it)
        })
        viewModel.email.observe(viewLifecycleOwner, Observer {
            binding.tbEmail.setText(it)
        })
        viewModel.password.observe(viewLifecycleOwner, Observer {
            binding.tbPassword.setText(it)
        })

        binding.registerButton.setOnClickListener {
            val fullName = viewModel.fullName.value
            val email = viewModel.email.value
            val password = viewModel.password.value
            viewModel.signUp(fullName!!, email!!, password!!)
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