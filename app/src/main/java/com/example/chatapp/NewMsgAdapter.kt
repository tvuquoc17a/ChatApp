package com.example.chatapp

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class NewMsgAdapter : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        //
    }

    override fun getLayout(): Int {
        return R.layout.user_new_message
    }
}