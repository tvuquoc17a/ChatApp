package registerlogin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import messages.LatestMessagesActivity
import models.User
import java.util.UUID

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //hide action bar
        supportActionBar?.hide()
        setDefaultAvatar()

        binding.btnRegister.setOnClickListener() {
            val email = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()

            //không cho nhập email/pass trống
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter text in email or password", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener() {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    Toast.makeText(this, "Create successfully", Toast.LENGTH_SHORT).show()
                    uploadImageToFirebaseStorage()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Create failed", Toast.LENGTH_SHORT).show()
                }
        }
        binding.tvAlreadyHaveAccount.setOnClickListener() {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        // chọn ảnh đại diện
        binding.btnSelectImageProfile.setOnClickListener() {
            Log.d("Main", "select photo")
            val intent = Intent(Intent.ACTION_PICK)
            //intennt type = image
            intent.type = "image/*"
            startActivityForResult(intent, 0)
            //display the image selected in the button background
        }
    }

    private fun setDefaultAvatar() {
        binding.circleImageviewRegister.setImageResource(R.drawable.default_avt)
        binding.btnSelectImageProfile.alpha = 0f
        selectedPhoto = Uri.parse("android.resource://com.example.chatapp/drawable/default_avt")
    }

    private var selectedPhoto : Uri? = null

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // default image is default_avt and when user chose image, it will be change

        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            Log.d("Main", "Photo was select")
            selectedPhoto = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhoto)
            //set image chosed circle like button background
            binding.circleImageviewRegister.setImageBitmap(bitmap)
            binding.btnSelectImageProfile.alpha = 0f
        }
    }

    private fun uploadImageToFirebaseStorage() {

        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")

        ref.putFile(selectedPhoto!!)
            .addOnSuccessListener{ it ->
                Log.d("RegisterActivity", "Upload photo successfully : ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity", "File location : $it")
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Please chose the photo", Toast.LENGTH_SHORT).show()
                TODO("Không chọn ảnh thì vẫn thêm user ?")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl : String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, binding.editTextTextName.text.toString(), profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Save user ${user.uid} to Firebase Database")
                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }
}