package com.example.socialmediaapp.Model

data class Comment(
   val text:String ="",
    val author:User=User(),
    val time:Long =0L
)
