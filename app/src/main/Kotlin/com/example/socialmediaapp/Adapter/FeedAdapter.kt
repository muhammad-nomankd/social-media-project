    package com.example.socialmediaapp.Adapter

    import android.annotation.SuppressLint
    import android.content.Context
    import android.content.Intent
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageView
    import android.widget.TextView
    import android.widget.Toast
    import androidx.core.content.ContextCompat
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide
    import com.example.socialmediaapp.Comment_Activity
    import com.example.socialmediaapp.Model.Post
    import com.example.socialmediaapp.R
    import com.firebase.ui.firestore.FirestoreRecyclerAdapter
    import com.firebase.ui.firestore.FirestoreRecyclerOptions
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.FirebaseFirestore
    import java.text.SimpleDateFormat
    import java.util.Date
    import java.util.TimeZone

    class FeedAdapter(options:FirestoreRecyclerOptions<Post>, val context: Context): FirestoreRecyclerAdapter<Post, FeedAdapter.FeedViewholder>(options) {
        class FeedViewholder(itemview: View):RecyclerView.ViewHolder(itemview){
                val  postimage:ImageView = itemView.findViewById(R.id.post_image)
                val posttext:TextView = itemView.findViewById(R.id.post_text)
                val post_time:TextView = itemView.findViewById(R.id.post_time)
                val postauthor:TextView = itemView.findViewById(R.id.author)
                val like_icon:ImageView = itemView.findViewById(R.id.post_like)
                val like_counter:TextView = itemView.findViewById(R.id.post_like_counter)
                val comment_icon:ImageView = itemView.findViewById(R.id.post_comment_icon)
               val comment_counter:TextView=itemview.findViewById(R.id.post_comment_counter)

            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewholder {
           val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item,parent,false)
            return FeedViewholder(view)
        }

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        override fun onBindViewHolder(holder: FeedViewholder, position: Int, model: Post) {
            holder.posttext.text = model.text
            holder.postauthor.text = model.user.name
            holder.like_counter.text=model.likeslist.size.toString()
            holder.post_time.text=model.time.toString()

            Glide.with(context)
                .load(model.imageurl)
                .centerCrop()
                .placeholder(R.drawable.backround_image)
                .into(holder.postimage)

            val firestore = FirebaseFirestore.getInstance()
            val userid = FirebaseAuth.getInstance().currentUser?.uid
            val PostDocument = firestore.collection("Posts").document(snapshots.getSnapshot(holder.adapterPosition).id)
            PostDocument.collection("Comments").get().addOnCompleteListener {
                holder.comment_counter.text=it.result.size().toString()
            }
            PostDocument.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val post = it.result?.toObject(Post::class.java)
                    post?.likeslist?.let { list ->
                        if (list.contains(userid)) {
                            holder.like_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.filled_like_icon))
                        } else {
                            holder.like_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like_icon_outlined))
                        }
                        holder.like_icon.setOnClickListener {
                            if (post.likeslist.contains(userid)) {
                                post.likeslist.remove(userid)
                                holder.like_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like_icon_outlined))

                            } else {
                                userid?.let { userid ->
                                    post.likeslist.add(userid)
                                    holder.like_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.filled_like_icon))
                                }
                                PostDocument.set(post)
                            }
                        }

                    }
                }else{
                    Toast.makeText(context,"Something went wrong. Please try once more.",Toast.LENGTH_LONG).show()
                }
                holder.comment_icon.setOnClickListener {
                    val intent = Intent(context, Comment_Activity::class.java)
                    intent.putExtra("postId", snapshots.getSnapshot(holder.adapterPosition).id)
                    context.startActivity(intent)
                }
                val dateformate = SimpleDateFormat("d MMMM yyyy ' at ' hh:mm a")
                dateformate.timeZone = TimeZone.getTimeZone("Asia/Karachi")
                val date = Date(model.time)
                val timestring=dateformate.format(date)
                holder.post_time.text=timestring
            }


        }
    }

