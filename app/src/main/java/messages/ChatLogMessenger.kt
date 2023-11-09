package messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.chatapp.ChatFromItem
import com.example.chatapp.ChatToItem
import com.example.chatapp.databinding.ActivityChatLogMessengerBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import models.User

class ChatLogMessenger : AppCompatActivity() {
    private lateinit var binding: ActivityChatLogMessengerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatLogMessengerBinding.inflate(layoutInflater)

        setContentView(binding.root)


        // get data from NewMessageActivity
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user?.username
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // hiển thị nút back
        val adapter = GroupAdapter<GroupieViewHolder>()
        adapter.add(ChatToItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())

        binding.recyclerviewChatLog.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // return to latest message activity
        when(item.itemId){
            android.R.id.home ->{
                val intent = Intent(this, LatestMessagesActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}