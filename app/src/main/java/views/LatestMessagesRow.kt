package views

import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView
import models.ChatMessage
import models.User

class LatestMessagesRow(private val chatMessage: ChatMessage?) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.tv_latest_message).text = chatMessage?.text

        val chatPartnerId : String = if(chatMessage?.fromId == FirebaseAuth.getInstance().uid)
        {
            chatMessage?.toId.toString()
        } else{
            chatMessage?.fromId.toString()
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                viewHolder.itemView.findViewById<TextView>(R.id.userName).text = user?.username
                val targetImage = viewHolder.itemView.findViewById<CircleImageView>(R.id.userImage_latest_message)
                Glide.with(viewHolder.itemView.context).load(user?.profileImageUrl).into(targetImage)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}