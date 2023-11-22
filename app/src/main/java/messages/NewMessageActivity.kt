package messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import models.User
import com.example.chatapp.UserItem
import com.example.chatapp.databinding.ActivityNewMessageBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class NewMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.show()
        supportActionBar?.title = "Select User"
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // hiển thị nút back
        fetchUsers()

    }

    // bắt sự kiện khi nhấn vào nút back
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                val intent = Intent(this, LatestMessagesActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
companion object {
    const val USER_KEY = "USER_KEY"
}
    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object  : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach{
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(User::class.java)

                    if(user != null){
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener(){ item, view ->//Khi nhấn vào người dùng nào trong new messenge thì mơ ô chat với ng đó
                    val intent = Intent(view.context, ChatLogMessenger::class.java)
                    //bring data(username) to chat log
                    val userItem = item as UserItem
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()
                }
                binding.recyclerviewNewmessage.adapter = adapter
            }
            override fun onCancelled(p0 : DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}