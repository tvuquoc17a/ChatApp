package messages

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.chatapp.R
import com.example.chatapp.R.id
import com.example.chatapp.databinding.ActivityLatestMessagesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import models.ChatMessage
import models.User
import registerlogin.RegisterActivity
import views.LatestMessagesRow

class LatestMessagesActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    private val adapter = GroupAdapter<GroupieViewHolder>()

    companion object {
        var currentUser: User? = null
    }

    private lateinit var binding: ActivityLatestMessagesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLatestMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerviewLatestMessages.adapter = adapter
        binding.recyclerviewLatestMessages.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerviewLatestMessages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)


        fun toolbarSetting() {
            val ref = FirebaseDatabase.getInstance().getReference("/users")
            ref.child(FirebaseAuth.getInstance().uid.toString()).child("username")
                .get()
                .addOnSuccessListener {
                    val userName = findViewById<TextView>(id.user_name)
                    userName.text = it.value.toString()
                }.addOnFailureListener {
                    Log.d("Latest", "Failed to get username")
                }
            val userImage = findViewById<ImageView>(R.id.user_image)
            // set user image in toolbar
            ref.child(FirebaseAuth.getInstance().uid.toString()).child("profileImageUrl")
                .get()
                .addOnSuccessListener {
                    Picasso.get().load(it.value.toString()).into(userImage!!)
                }.addOnFailureListener {
                    Log.d("Latest", "Failed to get user image")
                }
            userImage.setOnClickListener {
                //popupMenu.show()
                val popupMenu = PopupMenu(this, userImage)
                popupMenu.setOnMenuItemClickListener(this)
                popupMenu.inflate(R.menu.menu1)
                popupMenu.show()
            }
        }
        toolbarSetting()

        //mở chat log với người dùng khi ấn vào row trong latest activity
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatLogMessenger::class.java)
            val row = item as LatestMessagesRow
            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }
        listenForLatestMessages()
        fetchCurrentUser()
        verifyUserIsLogged()
    }

    val latestMessagesMap = LinkedHashMap<String, ChatMessage>()
    private val newLatestMessage = HashMap<String, ChatMessage>()


    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("latest-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage // Add message to the top
                //Log.d("onChildAdded", "Latest message: ${chatMessage.text}")
                refreshRecyclerView()
                if (chatMessage.fromId != FirebaseAuth.getInstance().uid) {
                    notify(chatMessage.text)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                val messageId = snapshot.key ?: return // Lấy khóa của tin nhắn

                latestMessagesMap.remove(messageId) // Loại bỏ tin nhắn khỏi danh sách
                latestMessagesMap[messageId] = chatMessage // Thêm tin nhắn mới vào đầu danh sách

                refreshRecyclerView()
                if (chatMessage.fromId != FirebaseAuth.getInstance().uid) {
                    notify(chatMessage.text)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun refreshRecyclerView() {
        adapter.clear()
        latestMessagesMap.values.forEach { chatMessage ->
            adapter.add(LatestMessagesRow(chatMessage))
        }
        binding.recyclerviewLatestMessages.adapter = adapter
        // Các cài đặt khác cho RecyclerView nếu cần thiết
    }


    private fun addRemainMessage() {
        newLatestMessage.values.forEach {
           adapter.add(LatestMessagesRow(it))
        }
    }

    fun notify(description: String) {
        // Tạo kênh thông báo
        val channel = NotificationChannel(
            "notify",
            "notify",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = description
        channel.setShowBadge(true)
        channel.enableVibration(true)
        channel.enableLights(true)

// Đăng ký kênh thông báo
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        // Tạo thông báo
        val notification = NotificationCompat.Builder(this, "notify")
            .setContentTitle("Tin nhắn mới")
            .setContentText(description)
            .setSmallIcon(R.drawable.baseline_email_24)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, LatestMessagesActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()

        // Hiển thị thông báo
        notificationManager.notify(1, notification)

    }

//    private fun addDumpData() {
//        Log.d("Latest", "adddumpdata")
//        val adapter = GroupAdapter<GroupieViewHolder>()
//        adapter.add(LatestMessagesRow())
//        adapter.add(LatestMessagesRow())
//        adapter.add(LatestMessagesRow())
//        binding.recyclerviewLatestMessages.adapter = adapter
//    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    //kiểm tra xem user đã đăng nhập hay chưa
    private fun verifyUserIsLogged() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_item_2 -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                return true
            }

            else -> false
        }

    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId){
//            R.id.menu_new_message -> {
//                val intent = Intent(this, NewMessageActivity::class.java)
//                startActivity(intent)
//            }
//            R.id.menu_sign_out ->{
//                FirebaseAuth.getInstance().signOut()
//                val intent = Intent(this, RegisterActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                startActivity(intent)
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    //tạo ra topmenu
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.nav_menu,menu)
//        return super.onCreateOptionsMenu(menu)
//    }
}

