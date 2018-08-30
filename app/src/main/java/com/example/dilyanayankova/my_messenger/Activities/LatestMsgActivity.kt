package com.example.dilyanayankova.my_messenger.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.view.Menu
import android.view.MenuItem
import com.example.dilyanayankova.my_messenger.Models.ChatMessage
import com.example.dilyanayankova.my_messenger.Models.LatestMsgRow
import com.example.dilyanayankova.my_messenger.Models.User
import com.example.dilyanayankova.my_messenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_msg.*
import kotlinx.android.synthetic.main.latest_msg_row.view.*
import android.R.attr.entries

class LatestMsgActivity : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
    }

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_msg)

        rycicler_latest_msg.adapter = adapter
        rycicler_latest_msg.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))//add separator
        //set item click listener
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatLogActivity::class.java) //the chat screen
            //item is the object we click on
            val row = item as LatestMsgRow
            intent.putExtra(NewMsgActivity.USERKEY, row.chatPartnerUser)
            startActivity(intent)
        }

        listenForLatestMsg()

        verifyUserIsLoggedIn()

        fetchCurrentUser()
    }

    //in order to change a msg when it is changed in Firebase not to create  a new one every time
    val latestsMsgMap = HashMap<String, ChatMessage>()

    private fun refreshRecyclerView() {
        adapter.clear()
        latestsMsgMap.values.forEach {
            adapter.add(LatestMsgRow(it))//it refers to item of loop
        }
    }

    private fun listenForLatestMsg() {
        val fromId = FirebaseAuth.getInstance().uid

        val ref = FirebaseDatabase.getInstance().getReference("/latest-msg/$fromId")
        //listen for new events
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMsg = p0.getValue(ChatMessage::class.java) ?: return
                if (chatMsg.fromId == chatMsg.toId)return//user cannot write to himself
                latestsMsgMap[p0.key!!] = chatMsg
                refreshRecyclerView()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                //called everytime a msg is changed
                val chatMsg = p0.getValue(ChatMessage::class.java) ?: return
                if (chatMsg.fromId == chatMsg.toId)return
                adapter.add(LatestMsgRow(chatMsg))
                latestsMsgMap[p0.key!!] = chatMsg
                refreshRecyclerView()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                //p0 is our data snapchat
                currentUser = p0.getValue(User::class.java)
            }
        })
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            //if user is not registered!!!
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK) //launch the latestmsgactivity every time we create a new user
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        //when we click on the option in nav_menu we fire up this method
        when (item?.itemId) { //switch case in Kotlin
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK) //launch the latestmsgactivity every time we create a new user
                startActivity(intent)
            }
            R.id.menu_new_msg -> {
                val intent = Intent(this, NewMsgActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)//in directory menu
        return super.onCreateOptionsMenu(menu)
    }
}