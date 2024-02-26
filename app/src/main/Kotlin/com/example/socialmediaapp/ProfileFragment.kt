@file:Suppress("OVERRIDE_DEPRECATION")

package com.example.socialmediaapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.socialmediaapp.Model.User
import com.example.socialmediaapp.Utils.UserUtils
import com.example.socialmediaapp.auth.AuthenticationActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    private lateinit var profileImg: CircleImageView
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileImg = view.findViewById(R.id.profile_image)
        val profile_name: EditText = view.findViewById(R.id.profile_name)
        val profile_bio: EditText = view.findViewById(R.id.profile_bio)
        val save_btn: Button = view.findViewById(R.id.profile_save_button)
        val log_out_btn: Button = view.findViewById(R.id.log_out_button)

        profile_name.setText(UserUtils.user?.name)
        profile_bio.setText(UserUtils.user?.bio)

        profileImg.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        Glide.with(this)
            .load(UserUtils.user?.imageUrl)
            .placeholder(R.drawable.person_icon)
            .centerCrop()
            .into(profileImg)

        save_btn.setOnClickListener {
            val new_user_name = profile_name.text.toString()
            val bio = profile_bio.text.toString()

            if (new_user_name.isEmpty()) {
                Toast.makeText(activity, "Name should not be empty.", Toast.LENGTH_SHORT).show()
            } else {
                val userdocument =
                    FirebaseFirestore.getInstance().collection("User").document(UserUtils.user?.id!!)
                UserUtils.user?.let { user ->
                    val updatedUser = User(
                        id = user.id,
                        name = new_user_name,
                        email = user.email,
                        followers = user.followers,
                        bio = bio,
                        imageUrl = user.imageUrl
                    )

                    userdocument.set(updatedUser).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(activity, "Details updated successfully.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            UserUtils.getCurrentUser()
        }

        log_out_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, AuthenticationActivity::class.java)
            context?.startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            profileImg.setImageURI(fileUri)
            imageUri = fileUri
            addUserImage()
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(activity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addUserImage() {
        val firestore = FirebaseFirestore.getInstance()

        // Check if imageUri is not null
        imageUri?.let { uri ->
            val storage = FirebaseStorage.getInstance().reference.child("Images")
                .child("${UserUtils.user?.email}_${System.currentTimeMillis()}.jpg")

            val uploadTask = storage.putFile(uri)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    Log.d("Upload task", task.exception.toString())
                }
                storage.downloadUrl
            }.addOnCompleteListener { urlTaskCompleted ->
                if (urlTaskCompleted.isSuccessful) {
                    val downloadUrl = urlTaskCompleted.result
                    UserUtils.user?.let { user ->
                        val newUser = User(
                            id = user.id,
                            name = user.name,
                            email = user.email,
                            followers = user.followers,
                            bio = user.bio,
                            imageUrl = downloadUrl.toString()
                        )

                        firestore.collection("User").document(user.id).set(newUser)
                            .addOnCompleteListener { saved ->
                                if (saved.isSuccessful) {
                                    UserUtils.getCurrentUser()
                                    Toast.makeText(activity, "Image uploaded", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "Error occurred, please try again.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(activity, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Toast.makeText(activity, "Image URI is null.", Toast.LENGTH_SHORT).show()
        }
    }
}
