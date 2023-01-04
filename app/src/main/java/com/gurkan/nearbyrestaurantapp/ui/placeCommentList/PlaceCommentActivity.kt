package com.gurkan.nearbyrestaurantapp.ui.placeCommentList

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.gurkan.nearbyrestaurantapp.databinding.ActivityPlaceCommentBinding
import com.gurkan.nearbyrestaurantapp.model.Comment
import com.gurkan.nearbyrestaurantapp.ui.comment.commentList.CommentListAdapter
import com.gurkan.nearbyrestaurantapp.ui.details.specialPlaceName
import java.util.ArrayList
class PlaceCommentActivity : AppCompatActivity() {
    private lateinit var placeCommentRecyclerView: RecyclerView
    private lateinit var binding: ActivityPlaceCommentBinding
    private lateinit var viewModel: PlaceCommentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(PlaceCommentViewModel::class.java)
        placeCommentRecyclerView = binding.rvPlaceComment
        placeCommentRecyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.placeCommentList.observe(this, Observer { comments ->
            placeCommentRecyclerView.adapter = PlaceCommentAdapter(comments, specialPlaceName)
        })

        viewModel.getCommentData()
    }
}