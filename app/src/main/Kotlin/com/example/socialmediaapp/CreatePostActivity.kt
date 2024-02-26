package com.example.socialmediaapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.socialmediaapp.Model.Post
import com.example.socialmediaapp.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class CreatePostActivity : AppCompatActivity() {
    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val TAG = "CreatePostActivity"
    }

    lateinit var progressbar: ProgressBar
    lateinit var postimage: ImageView
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_create_post)

        postimage = findViewById(R.id.postimage)
        val posttext: TextView = findViewById(R.id.post_text)
        val postbutton: Button = findViewById(R.id.post_button)
        val ovalShapeDrawable = ContextCompat.getDrawable(this, R.drawable.image_shape)
        progressbar = findViewById(R.id.progessbar)

        postimage.background = ovalShapeDrawable
        postimage.clipToOutline = true

        postimage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        postbutton.setOnClickListener {
            val text = posttext.text.toString()
            if (TextUtils.isEmpty(text)) {
                Toast.makeText(this, "Please write some description of the text.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (imageUri != null) {
                addPost(text)
                progressbar.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                progressbar.visibility = View.GONE
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            if (data != null && data.data != null) {
                imageUri = data.data
                Glide.with(this)
                    .load(imageUri)
                    .into(postimage)
            } else {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Task is cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    // ...

    private fun addPost(text: String) {
        val firestore = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            Log.e(TAG, "Current user is null")
            Toast.makeText(this, "Current user is null", Toast.LENGTH_SHORT).show()
            progressbar.visibility = View.GONE
            return
        }

        val userId = currentUser.uid

        firestore.collection("User")
            .document(userId)
            .get()
            .addOnCompleteListener { userTask ->
                if (userTask.isSuccessful) {
                    val user = userTask.result.toObject(User::class.java)

                    if (user != null) {
                        val storageReference = FirebaseStorage.getInstance().reference
                            .child("Image")
                            .child("${currentUser.email}-${System.currentTimeMillis()}.jpg")

                        val uploadTask = storageReference.putFile(imageUri!!)

                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                throw task.exception!!
                            }
                            storageReference.downloadUrl
                        }.addOnCompleteListener { urlTaskCompleted ->
                            if (urlTaskCompleted.isSuccessful) {
                                val downloadUri = urlTaskCompleted.result
                                val post = Post(text, downloadUri.toString(), user, System.currentTimeMillis())
                                firestore.collection("Posts")
                                    .add(post)
                                    .addOnCompleteListener { posted ->
                                        if (posted.isSuccessful) {
                                            Toast.makeText(this, "Post added Successfully", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(this,MainActivity::class.java)
                                            startActivity(intent)

                                        } else {
                                            Log.e(TAG, "Error adding post: ${posted.exception}")
                                            Toast.makeText(this, "Something went wrong, please try again later.", Toast.LENGTH_SHORT).show()
                                        }
                                        progressbar.visibility = View.GONE
                                    }
                            } else {
                                Log.e(TAG, "Error getting download URL: ${urlTaskCompleted.exception}")
                                Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show()
                                progressbar.visibility = View.GONE
                            }
                        }
                    } else {
                        Log.e(TAG, "User data is null")
                        Toast.makeText(this, "User data is null", Toast.LENGTH_SHORT).show()
                        progressbar.visibility = View.GONE
                    }
                } else {
                    Log.e(TAG, "Error getting user data: ${userTask.exception}")
                    Toast.makeText(this, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show()
                    progressbar.visibility = View.GONE
                }
            }
    }

}
