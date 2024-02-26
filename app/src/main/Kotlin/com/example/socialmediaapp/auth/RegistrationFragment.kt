package com.example.socialmediaapp.auth

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.socialmediaapp.Model.User
import com.example.socialmediaapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class RegistrationFragment : Fragment() {
     companion object{
         const val TAG = "RegisterFragment"
     }
    val password_rejex = Regex("\"^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}\$\"")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailtxt:EditText = view.findViewById(R.id.loginmail)
        val nametxt:EditText = view.findViewById(R.id.name)
        val passwordtxt:EditText = view.findViewById(R.id.loginpassword)
        val cnfmpasstxt:EditText = view.findViewById(R.id.confirm_pass)

        val signupbtn:Button = view.findViewById(R.id.signupbutton)
        val gotosignup: TextView = view.findViewById(R.id.have_an_accout)
        val signup_progress_bar:ProgressBar = view.findViewById(R.id.signup_progressbar)


       signupbtn.setOnClickListener {
           val email = emailtxt.editableText?.toString()
           val name = nametxt.editableText?.toString()
           val pass = passwordtxt.editableText?.toString()
           val confmpass = cnfmpasstxt.editableText?.toString()
                emailtxt.error = null
                nametxt.error = null
                passwordtxt.error = null
                cnfmpasstxt.error = null
           if (TextUtils.isEmpty(name)){
               nametxt.error = "Enter your name."
               return@setOnClickListener
           }
           if (TextUtils.isEmpty(email)){
               emailtxt.error = "Email is empty."
               return@setOnClickListener
           }
           if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
               emailtxt.error = "Enter a valid email address."
               return@setOnClickListener
           }

           if (TextUtils.isEmpty(pass)){
               passwordtxt.error = "Password can't be null."
               return@setOnClickListener
           }


               if (pass!!.matches(password_rejex)){
                   passwordtxt.error = "Password should be Minimum eight characters, at least one letter, one number and one special character"
                   return@setOnClickListener
               }


           if (TextUtils.isEmpty(confmpass)){
               cnfmpasstxt.error = "Confirm password."
               return@setOnClickListener
           }
           if(pass!=confmpass){
               cnfmpasstxt.error = "Password does'nt match."
               return@setOnClickListener
           }
           signup_progress_bar.visibility = View.VISIBLE

           val auth=FirebaseAuth.getInstance()

           auth.createUserWithEmailAndPassword(email.toString(),pass.toString())
               .addOnCompleteListener{task->
                   if (task.isSuccessful){
                       val user = User(auth.currentUser?.uid!!, name.toString(), email.toString())
                       val firestore = FirebaseFirestore.getInstance().collection("User")
                       firestore.document(auth.currentUser?.uid!!).set(user)
                           .addOnCompleteListener{task2->
                               if (task2.isSuccessful){
                                   Toast.makeText(context,"Registered",Toast.LENGTH_SHORT).show()
                               }
                               else{
                                   Log.d(TAG,task2.exception.toString())
                               }
                           }
                       signup_progress_bar.visibility = View.GONE
                   } else{
                       signup_progress_bar.visibility = View.GONE
                       Toast.makeText(context,"Something went wrong please try entering different email.",Toast.LENGTH_LONG).show()
                       Log.d(TAG , task.exception.toString())

                   }
               }

       }
        gotosignup.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.auth_fragment_container, LoginFragment())
                ?.addToBackStack(null)
                ?.commit()
        }
}}