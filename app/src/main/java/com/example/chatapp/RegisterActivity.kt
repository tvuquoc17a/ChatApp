package com.example.chatapp

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.databinding.ActivityRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                    Toast.makeText(this, "Create succesfully", Toast.LENGTH_SHORT).show()
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

    var selectedPhoto : Uri? = null
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
        if(selectedPhoto == null) return

        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")

        ref.putFile(selectedPhoto!!)
            .addOnSuccessListener{
                Log.d("RegisterActivity", "Upload photo succesfully : ${it.metadata?.path}")

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
            }
    }
}