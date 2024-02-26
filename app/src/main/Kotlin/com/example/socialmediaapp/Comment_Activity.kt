package com.example.socialmediaapp

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapp.Adapter.Comments_Adapter
import com.example.socialmediaapp.Model.Comment
import com.example.socialmediaapp.Utils.UserUtils
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class Comment_Activity : AppCompatActivity() {
    private var postid:String?=null
    private var commentsadapter:Comments_Adapter?=null
    private lateinit var CommentRecyclerView:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
         supportActionBar?.hide()
        postid = intent.getStringExtra("postId")
        CommentRecyclerView = findViewById(R.id.comment_recycler_view)
        SetUpRecyclerView()

        val sendbtn:ImageView=findViewById(R.id.send_comment_icon)
        val CommentEtxt:EditText=findViewById(R.id.enter_comment)

        sendbtn.setOnClickListener {
            val firestore=FirebaseFirestore.getInstance()
            val commenttxt = CommentEtxt.text.toString()
            val comment=Comment(commenttxt,UserUtils.user!!,System.currentTimeMillis())
            firestore.collection("Posts").document(postid!!).collection("Comments").document().set(comment)
            CommentEtxt.text.clear()
        }
    }

    private fun SetUpRecyclerView(){
        val Firestore = FirebaseFirestore.getInstance()
        val query=postid?.let{
            Firestore.collection("Posts").document(it).collection("Comments")
        }
        val RecyclerViewoption = query?.let {
            FirestoreRecyclerOptions.Builder<Comment>().setQuery(it,Comment::class.java).build()
        }
        commentsadapter = RecyclerViewoption?.let {
             Comments_Adapter(it,this)
        }
        CommentRecyclerView.adapter = commentsadapter
        CommentRecyclerView.layoutManager=LinearLayoutManager(this)

    }

    override fun onStart() {
        super.onStart()
        commentsadapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        commentsadapter?.stopListening()
    }

    }