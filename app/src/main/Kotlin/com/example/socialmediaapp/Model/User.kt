package com.example.socialmediaapp.Model

data class User(val id:String = "",
                val name:String="",
                val email:String="",
                val followers:MutableList<String> = mutableListOf(),
                val bio:String="",
                val imageUrl:String="")
