package com.example.socialmediaapp.auth

import android.content.Intent
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
import com.example.socialmediaapp.MainActivity
import com.example.socialmediaapp.R
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    companion object{
        const val TAG = "LoginFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gotoregister:TextView = view.findViewById(R.id.doesenthaveanaccount)
        gotoregister.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.auth_fragment_container, RegistrationFragment())
                ?.commit()

        }

        val emailtext:EditText = view.findViewById(R.id.loginmail)
        val passwordtext:EditText = view.findViewById(R.id.loginpassword)


        val loginbutton:Button = view.findViewById(R.id.loginbtn)
        val loginprogressbar:ProgressBar=view.findViewById(R.id.sign_in_progressbar)

        loginbutton.setOnClickListener {
            val email = emailtext.editableText?.toString()
            val pass = passwordtext.editableText?.toString()
            emailtext.error = null
            passwordtext.error = null

            if (TextUtils.isEmpty(email)){
                emailtext.error = "Email is empty."
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailtext.error = "Enter a valid email address."
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(pass)){
                passwordtext.error = "Password can't be null."
                return@setOnClickListener
            }

            loginprogressbar.visibility = View.VISIBLE
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email.toString(), pass.toString())
                .addOnCompleteListener{task->
                    if (task.isSuccessful){
                       startActivity(Intent(activity, MainActivity::class.java))
                        loginprogressbar.visibility = View.GONE
                    }
                    else{
                        val errorMessage = task.exception?.message
                        Toast.makeText(context, "Authentication failed: $errorMessage", Toast.LENGTH_LONG).show()
                        Log.d(TAG, "Authentication failed: $errorMessage")
                        loginprogressbar.visibility = View.GONE

                    }

                }

        }
    }

}