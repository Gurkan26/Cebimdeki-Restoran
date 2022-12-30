package com.gurkan.nearbyrestaurantapp.ui.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.gurkan.nearbyrestaurantapp.R
import com.gurkan.nearbyrestaurantapp.databinding.FragmentRegisterBinding

class FragmentRegister : Fragment() {

    private lateinit var auth: FirebaseAuth
    var databaseReference: DatabaseReference? = null
    var database: FirebaseDatabase? = null
    lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("profile")

        binding.registerButton.setOnClickListener {
            var fullName = binding.tbFullName.text.toString()
            var eMail = binding.tbEmail.text.toString()
            var password = binding.tbPassword.text.toString()

            if (TextUtils.isEmpty(fullName)) {
                binding.tbFullName.error = getString(R.string.loginNotEmpty)
                return@setOnClickListener
            } else if (TextUtils.isEmpty(eMail)) {
                binding.tbEmail.error = getString(R.string.loginNotEmpty)
                return@setOnClickListener
            } else if (TextUtils.isEmpty(password)) {
                binding.tbPassword.error = getString(R.string.passNoEmpty)
                return@setOnClickListener
            }
            else{

            // Kullanıcı bilgilerini Firebase'e atma
            auth.createUserWithEmailAndPassword(
                binding.tbEmail.text.toString(),
                binding.tbPassword.text.toString()
            ) //Auth kısmına kullanıcıyı ekledim.
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        //Kullanıcının bilgilerini Firebase idsini kullanarak ekliyoz

                        var currentUser = auth.currentUser
                        var currentUserDb = currentUser?.let { it1 ->
                            databaseReference?.child(it1.uid) // database reference ile bi child oluşturduk. ve bu childin kullanıcı idsini burada aldım.
                        }
                        currentUserDb?.child("fullName")
                            ?.setValue(binding.tbFullName.text.toString())
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.success),
                            Toast.LENGTH_LONG
                        ).show()
                        findNavController().navigate(FragmentRegisterDirections.actionFragmentRegisterToFragmentLogin())
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.error),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

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