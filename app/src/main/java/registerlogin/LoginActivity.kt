package registerlogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.chatapp.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import messages.LatestMessagesActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnLogin.setOnClickListener(){
            val email = binding.edtLoginEmail.text.toString()
            val password = binding.edtLoginPassword.text.toString()

            //không cho nhập email/pass trống
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter text in email or password", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener() {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Login succesfully", Toast.LENGTH_SHORT).show()
                    getToken()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                }
        }
        binding.backToRegister.setOnClickListener(){
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM TOKEN Failed", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            Log.d("FCM TOKEN", token)
        })
    }
}


