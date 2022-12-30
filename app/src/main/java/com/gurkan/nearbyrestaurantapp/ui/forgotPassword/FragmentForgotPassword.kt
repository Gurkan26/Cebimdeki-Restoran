package com.gurkan.nearbyrestaurantapp.ui.forgotPassword

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


    lateinit var binding: FragmentForgotPasswordBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        binding.btnReset.setOnClickListener {
            var pResetMail = binding.tbLoginInput.text.toString().trim()
            if (TextUtils.isEmpty(pResetMail)) {
                binding.tbLoginInput.error = getString(R.string.forgotPassCannotEmpty)
            } else{
                auth.sendPasswordResetEmail(pResetMail)
                    .addOnCompleteListener(requireActivity()){resetMail->
                        if (resetMail.isSuccessful){
                            Toast.makeText(requireContext(),getString(R.string.forgotPassResetLink),Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(requireContext(),getString(R.string.forgotPassSendFailed),Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
        binding.btnLoginPage.setOnClickListener {
            findNavController().navigate(FragmentForgotPasswordDirections.actionFragmentForgotPasswordToFragmentLogin())
        }

        return binding.root
    }


}