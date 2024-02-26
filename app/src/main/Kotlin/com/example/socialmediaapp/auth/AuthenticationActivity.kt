package com.example.socialmediaapp.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.socialmediaapp.R

class AuthenticationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_authentication)
        supportFragmentManager.beginTransaction()
            .replace(R.id.auth_fragment_container, LoginFragment())
            .commit()
    }
}