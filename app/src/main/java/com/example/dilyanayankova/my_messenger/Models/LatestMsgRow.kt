package com.example.dilyanayankova.my_messenger.Models

import com.example.dilyanayankova.my_messenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_msg_row.view.*

class LatestMsgRow(val chatMessage: ChatMessage) : Item<ViewHolder>() {
    var chatPartnerUser: User? = null

    override fun getLayout(): Int {
        return R.layout.latest_msg_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_latest_msg.text = chatMessage.text
        //set the username into latest msg username
        val chatPartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.textView_username_latest_msg.text = chatPartnerUser?.username

                //set image into latest msg image
                val targetImgView = viewHolder.itemView.imageView_lts_msg
                Picasso.get().load(chatPartnerUser?.profileImgUrl).into(targetImgView)
            }
        })
    }
}