package com.example.chatapp

import com.xwray.groupie.GroupieViewHolder

class ChatToItem() : com.xwray.groupie.Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_item
    }
}