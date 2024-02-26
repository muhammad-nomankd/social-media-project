package com.example.socialmediaapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapp.Adapter.Chat_Adapter
import com.example.socialmediaapp.Model.Messege
import com.example.socialmediaapp.Utils.UserUtils
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatFragment : Fragment() {

    var ChatroomId:String?=null

    lateinit var chatadapter:Chat_Adapter
    lateinit var chatrecyclerview:RecyclerView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bundle=this.arguments

        if (bundle!=null) {
            ChatroomId = bundle.getString("ChatroomId")
        }
     chatrecyclerview=view.findViewById(R.id.Chat_recycler_view)
        setuprecyclerView()


        val enteredMessege:EditText=view.findViewById(R.id.enter_messege)
        val sendMessege:ImageView=view.findViewById(R.id.send_messege_icon)

        sendMessege.setOnClickListener {
            if (enteredMessege.text.isNullOrEmpty())
                return@setOnClickListener
        val chattext=enteredMessege.text.toString()

            val firestore=FirebaseFirestore.getInstance()
                .collection("ChatRooms").document(ChatroomId!!).collection("Messages")
        val chat=Messege(chattext,UserUtils.user!!,System.currentTimeMillis(),ChatroomId!!)
        firestore.document().set(chat).addOnCanceledListener {
            chatrecyclerview.scrollToPosition(chatrecyclerview.adapter?.itemCount!! - 1)
            enteredMessege.text.clear()
        }
        }
    }
    fun setuprecyclerView(){
        val firestore=FirebaseFirestore.getInstance()
        val query=firestore.collection("ChatRooms").document(ChatroomId!!).collection("Messages")
            .orderBy("time",Query.Direction.ASCENDING)

        val RecyclerViewOptions=FirestoreRecyclerOptions.Builder<Messege>().setQuery(query,Messege::class.java).build()

        chatadapter= Chat_Adapter(RecyclerViewOptions)

        chatrecyclerview.adapter=chatadapter
        chatrecyclerview.layoutManager=LinearLayoutManager(context)



    }

    override fun onStart() {
        super.onStart()
        chatadapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        chatadapter.stopListening()
    }


}