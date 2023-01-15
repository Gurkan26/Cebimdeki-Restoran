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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gurkan.nearbyrestaurantapp.R

class FragmentLogin : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding = FragmentLoginBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        // Kullanıcının oturum açıp açmadığını kontrol ediyoruz

        val currentUser = auth.currentUser
        if (currentUser != null) { // giriş yapılmışsa maps activitye yönlendir.
            val maps = Intent(requireActivity(), MapsActivity::class.java)
            startActivity(maps)
        }
        // Giriş yap butonuna tıkladığında
        binding.buttonLogin.setOnClickListener {
            val loginMail = binding.tbLoginMail.text.toString()
            val loginPassword = binding.tbLoginPassword.text.toString()

            if (TextUtils.isEmpty(loginMail)) {
                binding.tbLoginMail.error = getString(R.string.loginNotEmpty)
                return@setOnClickListener
            } else if (TextUtils.isEmpty(loginPassword)) {
                binding.tbLoginPassword.error = getString(R.string.passNoEmpty)
                return@setOnClickListener
            }
            // giriş bilgileri doğruysa giriş işlemi burada dönecek
            else {
                loginViewModel.loginResult.observe(viewLifecycleOwner, Observer {
                    if (it == true) {

                        // Giriş başarılı, MapsActivity'e yönlendirin
                        val intent = Intent(requireActivity(), MapsActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Giriş başarısız, bir Toast mesajı gösterin
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.loginTryAgain),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })


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