package com.example.socialmediaapp.Model

data class Post(
    val text:String = "",
    val imageurl:String? = null,
    val user:User = User(),
    val time:Long = 0L,
    val likeslist:MutableList<String> = mutableListOf(),
    val comments:List<Comment> = emptyList()
)
