package com.gurkan.nearbyrestaurantapp.ui.comment.commentList

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.gurkan.nearbyrestaurantapp.databinding.FragmentCommentListBinding
import com.gurkan.nearbyrestaurantapp.model.Comment
import java.util.ArrayList


class FragmentCommentList : Fragment() {
    lateinit var binding: FragmentCommentListBinding
    private lateinit var commentRecyclerView: RecyclerView
    lateinit var commentList: ArrayList<Comment>
    private lateinit var dbref: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCommentListBinding.inflate(inflater, container, false)

        commentRecyclerView = binding.rvComment
        commentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        commentRecyclerView.setHasFixedSize(true)

        commentList = arrayListOf<Comment>()

        getCommentData()


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
                        commentList.add(comment!!)
                    }
                    commentRecyclerView.adapter = CommentListAdapter(commentList, requireContext())
                } else {
                    Log.e("Tsg", "fdgfd")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


}