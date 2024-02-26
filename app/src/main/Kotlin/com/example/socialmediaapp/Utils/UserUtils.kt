package com.example.socialmediaapp.Utils

import com.example.socialmediaapp.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object UserUtils {
    var user:User?=null
    fun getCurrentUser(){
        if (FirebaseAuth.getInstance().currentUser !=null)
            FirebaseFirestore.getInstance().collection("User")
                .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                .get().addOnCompleteListener{
                    user=it.result?.toObject(User::class.java)
                }
    }
}