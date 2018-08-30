package com.example.dilyanayankova.my_messenger.Models

import com.example.dilyanayankova.my_messenger.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.row_new_msg.view.*

class UserItem(val user: User) : Item<ViewHolder>() { //items for newmsgactivity class
    override fun bind(viewHolder: ViewHolder, position: Int) {
        //will be called in our list for each user object later on
        //to set the name of user
        viewHolder.itemView.username_new_msg.text = user.username
        //load the image of user
        Picasso.get().load(user.profileImgUrl).into(viewHolder.itemView.imageview_new_msg)
    }

    override fun getLayout(): Int {
        //render out each row for users
        return R.layout.row_new_msg
    }
}