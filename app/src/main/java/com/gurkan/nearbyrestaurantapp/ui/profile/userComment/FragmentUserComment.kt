package com.gurkan.nearbyrestaurantapp.ui.profile.userComment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gurkan.nearbyrestaurantapp.databinding.FragmentUserCommentsBinding
import com.gurkan.nearbyrestaurantapp.model.Comment
import com.gurkan.nearbyrestaurantapp.ui.profile.logout.specialuserName
import kotlin.collections.ArrayList

class FragmentUserComment : Fragment() {
    private lateinit var binding: FragmentUserCommentsBinding
    private lateinit var viewModel: UserCommentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserCommentsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[UserCommentViewModel::class.java]

        binding.rvUserCommentList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUserCommentList.setHasFixedSize(true)

        viewModel.comments.observe(viewLifecycleOwner, Observer { comments ->
            binding.rvUserCommentList.adapter =
                UserCommentAdapter(comments as ArrayList<Comment>, specialuserName)
        })

        viewModel.getCommentData()

        return binding.root
    }
}
