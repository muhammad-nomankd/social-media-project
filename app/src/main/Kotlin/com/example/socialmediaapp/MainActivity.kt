package com.example.socialmediaapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.socialmediaapp.Utils.UserUtils
import com.example.socialmediaapp.auth.AuthenticationActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (FirebaseAuth.getInstance().currentUser ==null) {
            val intent = Intent(this, AuthenticationActivity::class.java)
            startActivity(intent)
        }
        UserUtils.getCurrentUser()

        setfragment(FeedFragment())
            val bottomnavview:BottomNavigationView = findViewById(R.id.bottomnavigationview)
            bottomnavview.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.feed->{
                     setfragment(FeedFragment())
                    }
                    R.id.Search->{
                        setfragment(SearchFragment())
                    }
                    R.id.chatroom->{
                        setfragment(ChatRoomFragment())
                    }
                    R.id.profile->{
                        setfragment(ProfileFragment())
                    }

                }
                true

            }


    }
    private fun setfragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,fragment)
            .commit()
    }
}