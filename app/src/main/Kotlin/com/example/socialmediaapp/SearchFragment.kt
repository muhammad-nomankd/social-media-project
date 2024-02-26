
package com.example.socialmediaapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapp.Adapter.Search_Adapter
import com.example.socialmediaapp.Model.User
import com.example.socialmediaapp.Utils.UserUtils
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment() {


         lateinit var adapter:Search_Adapter
         lateinit var recyclerview:RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: Toolbar = view.findViewById(R.id.search_toolbar)
        toolbar.title = "Search user"

        (activity as? MainActivity)?.setSupportActionBar(toolbar)
        (activity as? MainActivity)?.supportActionBar?.show()

      setHasOptionsMenu(true)



        val firestore=FirebaseFirestore.getInstance()
        val query=firestore.collection("User").whereNotEqualTo("id",FirebaseAuth.getInstance().currentUser?.uid)

        val RecyclerViewoption=FirestoreRecyclerOptions.Builder<User>().setQuery(query,User::class.java).build()

        adapter= Search_Adapter(RecyclerViewoption)
        recyclerview=view.findViewById(R.id.search_recycler_view)

        recyclerview.adapter=adapter
        recyclerview.layoutManager=LinearLayoutManager(activity)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu,menu)
       val Searchvieww=SearchView(context!!)
        menu.findItem(R.id.action_search).actionView=Searchvieww
        Searchvieww.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                recyclerview.visibility=View.VISIBLE
                val firestore=FirebaseFirestore.getInstance()
                val newquery=firestore.collection("User").whereEqualTo("name",query)
                    .whereNotEqualTo("id",UserUtils.user?.id)
                val newrecyclerView=FirestoreRecyclerOptions.Builder<User>().setQuery(newquery,User::class.java).build()
                adapter.updateOptions(newrecyclerView)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                recyclerview.visibility=View.INVISIBLE
                return false
            }

        } )
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }


}
