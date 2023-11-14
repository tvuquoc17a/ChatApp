package views

import android.widget.ImageView
import android.widget.TextView
import com.example.chatapp.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import models.User

class ChatFromItem (val text : String, private val user : User?): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.tv_latest_message).text = text
        val uri  = user?.profileImageUrl
        val userImage = viewHolder.itemView.findViewById<ImageView>(R.id.imageView)
        Picasso.get().load(uri).into(userImage)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_item
    }
}