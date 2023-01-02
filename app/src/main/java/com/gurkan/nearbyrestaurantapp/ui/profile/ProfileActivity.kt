package com.gurkan.nearbyrestaurantapp.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.gurkan.nearbyrestaurantapp.MainActivity
import com.gurkan.nearbyrestaurantapp.R
import com.gurkan.nearbyrestaurantapp.databinding.ActivityProfileBinding
import com.gurkan.nearbyrestaurantapp.ui.comment.CommentActivity
import com.gurkan.nearbyrestaurantapp.ui.map.MapsActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var navController: NavController
lateinit var binding:ActivityProfileBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerViewProfile) as NavHostFragment
        navController = navHostFragment.navController


        binding.bottomNavigationView.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.mapsActivity -> {
                        startActivity(Intent(applicationContext, MapsActivity::class.java))
                        overridePendingTransition(0, 0)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.profileActivity -> {
                        startActivity(Intent(applicationContext, ProfileActivity::class.java))
                        overridePendingTransition(0, 0)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.commentActivity -> {
                        startActivity(Intent(applicationContext, CommentActivity::class.java))
                        overridePendingTransition(0, 0)
                        return@OnNavigationItemSelectedListener true
                    }

                }
                false
            })

    }
}