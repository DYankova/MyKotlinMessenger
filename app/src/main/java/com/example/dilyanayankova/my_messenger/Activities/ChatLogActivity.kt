package com.example.dilyanayankova.my_messenger.Activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.dilyanayankova.my_messenger.Models.ChatFromItem
import com.example.dilyanayankova.my_messenger.Models.ChatMessage
import com.example.dilyanayankova.my_messenger.Models.ChatToItem
import com.example.dilyanayankova.my_messenger.Models.User
import com.example.dilyanayankova.my_messenger.R
import com.example.dilyanayankova.my_messenger.R.id.recycler_view_chat_log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"//for Log.d
    }

    var toUser: User? = null
    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recycler_view_chat_log.adapter = adapter//allows to add an object in adapter, refresh list

        toUser = intent.getParcelableExtra<User>(NewMsgActivity.USERKEY) //take the user

        supportActionBar?.title = toUser?.username
        //render rows, get msg-s
        listenForMsg()

        sendbutton_chat_box.setOnClickListener {
            performMsg()
        }
    }

    private fun listenForMsg() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        //notify for every piece of data, real time data, refreshes
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMsg = p0.getValue(ChatMessage::class.java)

                if (chatMsg != null) {
                    //pump data in adapter
                    if (chatMsg.fromId == FirebaseAuth.getInstance().uid) { //where to be  the msg
                        val currentUser = LatestMsgActivity.currentUser ?: return
                        adapter.add(ChatFromItem(chatMsg.text, currentUser))
                    } else {
                        adapter.add(ChatToItem(chatMsg.text, toUser!!))
                    }
                    recycler_view_chat_log.scrollToPosition(adapter.itemCount - 1)//when we click on it to scroll to the end of the chat
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        }
        )
    }

    private fun performMsg() {
        //how to send msg in Firebaseсе
        val text = editText_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid //from id is the logged in user
        val toId = toUser!!.uid //to user
        if (fromId == null) return
        //save the msg for oth users
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()//push notificates us

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()//the ref for the user we send msg to!

        if (fromId == toId) return
        val chatMsg = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMsg)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved msg")
                    editText_chat_log.text.clear()
                    recycler_view_chat_log.scrollToPosition(adapter.itemCount - 1)//always scroll to the end
                }
        toReference.setValue(chatMsg)

        val latestMsgFrom = FirebaseDatabase.getInstance().getReference("/latest-msg/$fromId/$toId")

        latestMsgFrom.setValue(chatMsg)

        val latestMsgTo = FirebaseDatabase.getInstance().getReference("/latest-msg/$toId/$fromId")
        latestMsgTo.setValue(chatMsg)
    }
}

