package com.example.socialmediaapp.Model

data class ChatRoom(val name:String="",
                    val id:String="",
                    val time:Long=0L,
                    val image:User=User())
