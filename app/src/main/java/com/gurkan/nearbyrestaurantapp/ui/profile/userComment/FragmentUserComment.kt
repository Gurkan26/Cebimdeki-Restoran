package com.gurkan.nearbyrestaurantapp.ui.profile.userComment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.gurkan.nearbyrestaurantapp.databinding.FragmentUserCommentsBinding
import com.gurkan.nearbyrestaurantapp.model.Comment
import com.gurkan.nearbyrestaurantapp.ui.profile.logout.specialuserName
import java.util.ArrayList

class FragmentUserComment : Fragment() {

    private lateinit var userCommentRecyclerView: RecyclerView
    lateinit var userCommentList: ArrayList<Comment>
    private lateinit var dbref: DatabaseReference
    lateinit var binding: FragmentUserCommentsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserCommentsBinding.inflate(inflater, container, false)


        userCommentRecyclerView = binding.rvUserCommentList
        userCommentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userCommentRecyclerView.setHasFixedSize(true)
        userCommentList = arrayListOf<Comment>()
        getCommentData()
/**/

        return binding.root
    }

    private fun getCommentData() {


        dbref = FirebaseDatabase.getInstance().getReference("Comments")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())// var mı yok mu kontrol
                {
                    for (commentSnapshot in snapshot.children) {
                        val comment = commentSnapshot.getValue(Comment::class.java)
                        userCommentList.add(comment!!)
                    }
                    userCommentRecyclerView.adapter =
                        UserCommentAdapter(userCommentList, specialuserName)
                } else {
                    Log.e("FragmentUserComment", "Hata")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}
