package com.example.socialmediaapp.Model

data class Messege(
    val messege:String="",
    val author: User =User(),
    val time:Long=0L,
    val chatroomId:String="")
