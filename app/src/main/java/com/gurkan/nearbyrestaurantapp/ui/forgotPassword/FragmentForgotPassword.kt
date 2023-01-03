package com.gurkan.nearbyrestaurantapp.ui.forgotPassword

import android.content.ContentResolver
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.gurkan.nearbyrestaurantapp.R
import com.gurkan.nearbyrestaurantapp.databinding.FragmentForgotPasswordBinding

class FragmentForgotPassword : Fragment() {
    private val contentResolver: ContentResolver = requireActivity().contentResolver
    private lateinit var binding: FragmentForgotPasswordBinding
    private val viewModel = ForgotPasswordViewModel(contentResolver)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)

        binding.btnReset.setOnClickListener {
            if (TextUtils.isEmpty(binding.tbLoginInput.text.toString().trim())) {
                binding.tbLoginInput.error = getString(R.string.forgotPassCannotEmpty)
            } else {
                viewModel.email.value = binding.tbLoginInput.text.toString().trim()
                viewModel.sendPasswordResetEmail()
            }
        }
        binding.btnLoginPage.setOnClickListener {
            findNavController().navigate(FragmentForgotPasswordDirections.actionFragmentForgotPasswordToFragmentLogin())
        }

        return binding.root
    }
}
