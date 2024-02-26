package com.example.socialmediaapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapp.Model.Comment
import com.example.socialmediaapp.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class Comments_Adapter(options: FirestoreRecyclerOptions<Comment>, val context: Context): FirestoreRecyclerAdapter<Comment, Comments_Adapter.CommensViewHolder>(options) {
    class CommensViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val Commenttext: TextView = itemView.findViewById(R.id.comment_txt)
        val CommentAuthor: TextView = itemView.findViewById(R.id.comment_author)
        val CommentTime: TextView = itemView.findViewById(R.id.comment_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommensViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommensViewHolder(view)

    }

    override fun onBindViewHolder(holder: CommensViewHolder, position: Int, model: Comment) {
        holder.Commenttext.text = model.text
        holder.CommentAuthor.text = model.author.name

        val dateFormat = SimpleDateFormat("d MMMM yyyy ' at ' hh:mm a", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Karachi")
        val date = Date(model.time)
        val timeString = dateFormat.format(date)

        holder.CommentTime.text = timeString

    }



}