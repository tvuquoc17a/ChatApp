package com.example.chatapp

import android.widget.TextView
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView
import models.User

class UserItem(val user : User) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val userName : TextView = viewHolder.itemView.findViewById<TextView>(R.id.userName)
        userName.text = user.username
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView))
    }
    override fun getLayout(): Int {
        return  R.layout.user_new_message
    }


}