package com.example.socialmediaapp.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapp.Model.User
import com.example.socialmediaapp.R
import com.example.socialmediaapp.Utils.UserUtils
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView


class Search_Adapter(options:FirestoreRecyclerOptions<User>):FirestoreRecyclerAdapter<User,Search_Adapter.SearchViewHolder>(options){

    class SearchViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val User_image:CircleImageView=itemView.findViewById(R.id.profile_img)
        val user_name:TextView=itemView.findViewById(R.id.person_name)
        val follow_button:Button=itemView.findViewById(R.id.follow_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.user_item,parent,false)
        return SearchViewHolder(view)


    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int, model: User) {
        holder.user_name.text=model.name

        if (UserUtils.user?.followers?.contains(snapshots.getSnapshot(holder.adapterPosition).id) == true){
            holder.follow_button.text="Following"
        }else{
            holder.follow_button.text="Follow"
        }


        holder.follow_button.setOnClickListener {
            val firestore=FirebaseFirestore.getInstance()
            val  userDocument=firestore.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid!!)
            userDocument.get().addOnCompleteListener{
                if (it.isSuccessful){
                    val user=it.result?.toObject(User::class.java)
                    if (user?.followers!!.contains(snapshots.getSnapshot(holder.adapterPosition).id)){
                        user?.followers?.remove(snapshots.getSnapshot(holder.adapterPosition).id)
                        user?.let { it1->
                            userDocument.set(it1)
                        }
                        holder.follow_button.text="Follow"
                    }
                    else{
                        user?.followers.add(snapshots.getSnapshot(holder.absoluteAdapterPosition).id)

                        user?.let {
                            userDocument.set(it)
                        }
                        holder.follow_button.text="Following"



                    }


                }

            }
        }


    }

}