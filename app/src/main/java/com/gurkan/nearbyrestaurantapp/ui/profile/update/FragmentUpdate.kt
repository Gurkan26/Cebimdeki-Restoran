package com.gurkan.nearbyrestaurantapp.ui.profile.update

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.gurkan.nearbyrestaurantapp.R
import com.gurkan.nearbyrestaurantapp.databinding.FragmentUpdateBinding

class FragmentUpdate : Fragment() {

    lateinit var binding: FragmentUpdateBinding
    private lateinit var auth: FirebaseAuth
    private var databaseReference: DatabaseReference? = null
    private var database: FirebaseDatabase? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("profile")


        var currentUser = auth.currentUser
        binding.tbUpdateEmail.setText(currentUser?.email)

        var userReference = databaseReference?.child(currentUser?.uid!!)
        userReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                binding.tbUpdateFullName.setText(snapshot.child("fullName").value.toString())


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        binding.buttonUpdate.setOnClickListener {
            var updateMail = binding.tbUpdateEmail.text.toString().trim()
            currentUser!!.updateEmail(updateMail)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.updateEmailSucc),
                            Toast.LENGTH_LONG
                        ).show()

                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.updateEmailError),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }

            //Parola Güncelleme

            var updatePassword = binding.tbUpdatePassword.text.toString().trim()
            currentUser.updatePassword(updatePassword)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.updatePasswordSucc),
                            Toast.LENGTH_LONG
                        ).show()

                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.updatePasswordError),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }

            //Ad-Soyad Güncelleme

            var currentUserDb = currentUser?.let { it1 -> databaseReference?.child(it1.uid) }
            currentUserDb?.removeValue() // Id değerini kaldırıyoruz ve yeni bir isim ataması yapıyoruz.
            currentUserDb?.child("fullName")?.setValue(binding.tbUpdateFullName.text.toString())
            Toast.makeText(
                requireContext(),
                getString(R.string.updateFullNameSuccess),
                Toast.LENGTH_LONG
            ).show()
        }

        return binding.root
    }

}
