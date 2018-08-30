package com.example.dilyanayankova.my_messenger.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.dilyanayankova.my_messenger.Activities.LatestMsgActivity.Companion.currentUser
import com.example.dilyanayankova.my_messenger.Models.User
import com.example.dilyanayankova.my_messenger.Models.UserItem
import com.example.dilyanayankova.my_messenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_msg.*
import kotlinx.android.synthetic.main.row_new_msg.view.*

class NewMsgActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_msg)
        //change title of nav bar
        supportActionBar?.title = "Select User"
        //get users from Firebase
        val adapter = GroupAdapter<ViewHolder>()
        fetchUser()
    }

    companion object { //add static constants!
        val USERKEY = "USERKEY"
    }

    private fun fetchUser() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                //when we retrieve all the DB, p0 the whole data
                val adapter = GroupAdapter<ViewHolder>()//groupiadapter is used to avoid new class creating for adapter
                p0.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null && user.uid != FirebaseAuth.getInstance().uid) {
                        adapter.add(UserItem(user))//if new user add it to adapter
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USERKEY, userItem.user)//to use it in the nav bar title and to send it to chatlog
                    startActivity(intent)
                    finish() //if u go back with back btn
                }
                recyclerview_newmsg.adapter = adapter
            }
        })
    }
}