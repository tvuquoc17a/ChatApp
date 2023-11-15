package messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import views.ChatFromItem
import views.ChatToItem
import com.example.chatapp.databinding.ActivityChatLogMessengerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import models.ChatMessage
import models.User

class ChatLogMessenger : AppCompatActivity() {
    private lateinit var binding: ActivityChatLogMessengerBinding
    private var toUser : User? = null
    val adapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatLogMessengerBinding.inflate(layoutInflater)

        setContentView(binding.root)


        // get data from NewMessageActivity
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // hiển thị nút back

        listenForMessages()

        binding.btnSendButton.setOnClickListener() {
            performSendMessage()
            binding.edtNewMessage.clearFocus()
        }
        val adapter = GroupAdapter<GroupieViewHolder>()
        //addDumpData()

    }


    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d(
                        "listen", chatMessage.text.toString()
                    )// phân chia tin nhắn lệch sang 2 phía recyclerview
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatFromItem(chatMessage.text, LatestMessagesActivity.currentUser))
                    }else{
                        val toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
                        adapter.add(ChatToItem(chatMessage.text, toUser))
                    }
                    binding.recyclerviewChatLog.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun performSendMessage() {
        val text = binding.edtNewMessage.text.toString()
        val fromId = FirebaseAuth.getInstance().uid// lấy luôn id của người đang dung
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.uid
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()// đồng thời tạo 1 tin nhắn lưu ở phía bên kia
        val chatMessage = ChatMessage(
            text,
            reference.key.toString(),
            fromId.toString(),
            user?.uid.toString(),
            System.currentTimeMillis() / 1000
        )
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("Send", "send succesfully ${reference.key}")
                binding.edtNewMessage.setText("")
                binding.recyclerviewChatLog.scrollToPosition(adapter.itemCount-1)
            }
        toReference.setValue(chatMessage)
        val latestChatReference = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestChatReference.setValue(chatMessage)
        val latestChatToReference = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestChatToReference.setValue(chatMessage)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // return to latest message activity
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, LatestMessagesActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}