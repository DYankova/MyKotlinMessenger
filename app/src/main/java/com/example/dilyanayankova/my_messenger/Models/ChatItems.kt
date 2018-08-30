package com.example.dilyanayankova.my_messenger.Models

import com.example.dilyanayankova.my_messenger.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.notification_template_lines_media.view.*

class ChatFromItem(val text: String, val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_from.text = text

        val url = user.profileImgUrl ?: return
        val targetImgView = viewHolder.itemView.imageView_from ?: return
        Picasso.get().load(url).into(targetImgView) //load the img of user into chat
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String, val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_to.text = text

        //load our user image into the img
        val url = user.profileImgUrl
        val targetImgView = viewHolder.itemView.imageView_to
        Picasso.get().load(url).into(targetImgView) //load the img of user into chat
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}