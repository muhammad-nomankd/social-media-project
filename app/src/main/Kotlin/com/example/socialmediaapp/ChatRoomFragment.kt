package com.example.socialmediaapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapp.Adapter.ChatRoomAdapter
import com.example.socialmediaapp.Model.ChatRoom
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatRoomFragment : Fragment() {
    lateinit var chatroomadpterlv:ChatRoomAdapter
    lateinit var chatroomrecyclerview:RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatroomrecyclerview=view.findViewById(R.id.chat_room_recyclerView)
        SetUpRecyclerView()
        val fabutton:FloatingActionButton=view.findViewById(R.id.floating_action_button_chat)
        fabutton.setOnClickListener {
            val alertDialogue:AlertDialog.Builder=AlertDialog.Builder(context)
            val Edittext=EditText(context)
            alertDialogue.setTitle("Create Chatroom")
            alertDialogue.setMessage("Enter the name of the ChatRoom You want to create.")
            alertDialogue.setView(Edittext)
            var textentered=""
            alertDialogue.setPositiveButton("Create") { dialogInterface,i ->
                textentered=Edittext.text.toString()
                val document=FirebaseFirestore.getInstance().collection("ChatRooms").document()
                val chatroom=ChatRoom(textentered,document.id)
                document.set(chatroom)

            }
            alertDialogue.setNegativeButton("Cancel"){ dialogInterface,i->
                dialogInterface.dismiss()
            }
            alertDialogue.show()


        }
    }
   @SuppressLint("UseRequireInsteadOfGet")
   private fun SetUpRecyclerView(){
       val firestore=FirebaseFirestore.getInstance()
       val query=firestore.collection("ChatRooms").orderBy("name",Query.Direction.ASCENDING)
       val RecyclerVieoption=FirestoreRecyclerOptions.Builder<ChatRoom>().setQuery(query,ChatRoom::class.java).build()
       chatroomadpterlv= ChatRoomAdapter(RecyclerVieoption,activity!!)
       chatroomrecyclerview.adapter=chatroomadpterlv
       chatroomrecyclerview.layoutManager=LinearLayoutManager(context)

   }

    override fun onStart() {
        super.onStart()
        chatroomadpterlv.startListening()
    }

    override fun onStop() {
        super.onStop()
        chatroomadpterlv.stopListening()
    }

}