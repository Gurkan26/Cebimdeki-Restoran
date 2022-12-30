package com.gurkan.nearbyrestaurantapp.ui.profile.logout

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.gurkan.nearbyrestaurantapp.MainActivity
import com.gurkan.nearbyrestaurantapp.databinding.FragmentProfilLogoutBinding

var specialuserName = ""

class FragmentProfilLogout : Fragment() {

    lateinit var binding: FragmentProfilLogoutBinding
    private lateinit var auth: FirebaseAuth
    var databaseReference: DatabaseReference? = null
    var database: FirebaseDatabase? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfilLogoutBinding.inflate(inflater, container, false)



        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("profile")

        var currentUser = auth.currentUser
        binding.tbMail.text = "Email: " + currentUser?.email

        var userReference = databaseReference?.child(currentUser?.uid!!)
        userReference?.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {

                binding.tbFullName.text = "Adınız: " + snapshot.child("fullName").value.toString()
                specialuserName = snapshot.child("fullName").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {


            }
        })

        binding.btnExit.setOnClickListener {

            auth.signOut()
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