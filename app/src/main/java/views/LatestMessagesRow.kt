package views

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Typeface
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView
import models.ChatMessage
import models.User

class LatestMessagesRow(private val chatMessage: ChatMessage?) : Item<GroupieViewHolder>() {
    var chatPartnerUser : User? = null
    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {


        val chatPartnerId : String
        if(chatMessage?.fromId == FirebaseAuth.getInstance().uid)
        {
            chatPartnerId = chatMessage?.toId.toString()
            viewHolder.itemView.findViewById<TextView>(R.id.tv_latest_message).text = "You: " + chatMessage?.text

        } else{
            chatPartnerId = chatMessage?.fromId.toString()
            val targetTextView = viewHolder.itemView.findViewById<TextView>(R.id.tv_latest_message)
            targetTextView.text  = chatMessage?.text
            targetTextView.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java)
                viewHolder.itemView.findViewById<TextView>(R.id.userName).text = chatPartnerUser?.username
                val targetImage = viewHolder.itemView.findViewById<CircleImageView>(R.id.userImage_latest_message)
                Glide.with(viewHolder.itemView.context).load(chatPartnerUser?.profileImageUrl).into(targetImage)
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