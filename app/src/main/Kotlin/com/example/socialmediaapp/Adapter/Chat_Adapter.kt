package com.example.socialmediaapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapp.Model.Messege
import com.example.socialmediaapp.R
import com.example.socialmediaapp.Utils.UserUtils
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class Chat_Adapter(Options: FirestoreRecyclerOptions<Messege>) :
    FirestoreRecyclerAdapter<Messege, Chat_Adapter.ChatViewHolder>(Options) {

    companion object {
        const val Self_tex = 0
        const val Receiver_text = 1
    }

    class ChatViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val Messege_text: TextView = itemview.findViewById(R.id.sender_text)
        val Chat_author: TextView = itemview.findViewById(R.id.sender)
    }

    override fun getItemViewType(position: Int): Int {
        if (getItem(position).author.id == UserUtils.user?.id)
            return Self_tex
        else
            return Receiver_text

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        var view: View? = null
        if (viewType == Self_tex)
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_item_sender, parent, false)
        else
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_item_receiver, parent, false)
        return ChatViewHolder(view)

    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: Messege) {
        holder.Messege_text.text = model.messege
        holder.Chat_author.text = model.author.name

    }
}