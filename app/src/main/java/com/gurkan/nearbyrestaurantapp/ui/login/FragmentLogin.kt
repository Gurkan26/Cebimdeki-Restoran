package com.gurkan.nearbyrestaurantapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.gurkan.nearbyrestaurantapp.databinding.FragmentLoginBinding
import com.gurkan.nearbyrestaurantapp.ui.map.MapsActivity
import androidx.fragment.app.Fragment
import com.gurkan.nearbyrestaurantapp.R
class FragmentLogin : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        // Kullanıcının oturum açıp açmadığını kontrol ediyoruz

        var currentUser = auth.currentUser
        if (currentUser != null) { // giriş yapılmışsa maps activitye yönlendir.
            val maps = Intent(requireActivity(), MapsActivity::class.java)
            startActivity(maps)
        }
        // Giriş yap butonuna tıkladığında
        binding.buttonLogin.setOnClickListener {
            var loginMail = binding.tbLoginMail.text.toString()
            var loginPassword = binding.tbLoginPassword.text.toString()

            if (TextUtils.isEmpty(loginMail)) {
                binding.tbLoginMail.error = getString(R.string.loginNotEmpty)
                return@setOnClickListener
            } else if (TextUtils.isEmpty(loginPassword)) {
                binding.tbLoginPassword.error = getString(R.string.passNoEmpty)
                return@setOnClickListener
            }
            // giriş bilgileri doğruysa giriş işlemi burada dönecek
            else {
                auth.signInWithEmailAndPassword(loginMail, loginPassword)
                    .addOnCompleteListener(requireActivity()) {
                        if (it.isSuccessful) {
                            startActivity(
                                Intent(
                                    this@FragmentLogin.context,
                                    MapsActivity::class.java
                                )
                            )
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.loginTryAgain),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }

        }

        //Yeni üyelik sayfasına gitmek için
        binding.tbSignupNow.setOnClickListener {
            findNavController().navigate(FragmentLoginDirections.actionFragmentLoginToFragmentRegister())
        }

        binding.tbForgotPassword.setOnClickListener {
            findNavController().navigate(FragmentLoginDirections.actionFragmentLoginToFragmentForgotPassword())
        }

        return binding.root
    }


}