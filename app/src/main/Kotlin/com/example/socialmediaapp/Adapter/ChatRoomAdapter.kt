package com.example.socialmediaapp.Adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapp.ChatFragment
import com.example.socialmediaapp.MainActivity
import com.example.socialmediaapp.Model.ChatRoom
import com.example.socialmediaapp.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import de.hdodenhof.circleimageview.CircleImageView

class ChatRoomAdapter(options:FirestoreRecyclerOptions<ChatRoom>,val context:Context):FirestoreRecyclerAdapter<ChatRoom,ChatRoomAdapter.ChatRoomViewHolder>(options){
    class ChatRoomViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val chatRoom_name:TextView=itemView.findViewById(R.id.chatroom_item_name)
        val chat_author_image:CircleImageView=itemView.findViewById(R.id.person_img)
        val chat_author_bio:TextView=itemView.findViewById(R.id.chatroom_item_bio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.chatroom_item,parent,false)
        return ChatRoomViewHolder(view)

    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int, model: ChatRoom) {
        holder.chatRoom_name.text=model.name
        holder.chat_author_bio.text=model.image.bio
        holder.chat_author_image.setImageURI(model.image.imageUrl.toUri())


        holder.itemView.setOnClickListener{
            val bundle=Bundle()
            bundle.putString("ChatroomId",model.id)
            val chatfragment=ChatFragment()
            chatfragment.arguments=bundle

            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,chatfragment)
                .addToBackStack(null)
                .commit()
        }

    }
}