package com.example.dilyanayankova.my_messenger.Activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.dilyanayankova.my_messenger.Models.User
import com.example.dilyanayankova.my_messenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.chat_from_row.*
import java.util.*

//Base class for activities that use the <a href="{@docRoot}tools/extras/support-library.html
class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_btn.setOnClickListener {
            performRegister()
        }

        have_account_textView.setOnClickListener {
            //launch the login activity
            val intent = Intent(this, LoginActivity::class.java) //represent the class
            startActivity(intent)
        }

        selected_photo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoIri: Uri? = null
    //this method is called when we finish the intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //proceed and check what is the selected img

            selectedPhotoIri = data.data
            //to have access to the bitmap of the photo i selected
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoIri)

            select_photo_imgView.setImageBitmap(bitmap)
            selected_photo.alpha = 0f //hide the button when img is set
        }
    }

    private fun performRegister() {
        val email = email_edittext.text.toString()
        val passoword = password_edtiitext.text.toString()

        if (email.isEmpty() || passoword.isEmpty()) {
            Toast.makeText(this, "Please enter/ email/pw", Toast.LENGTH_SHORT).show()
            return
        }

        // Firebse Authentication
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, passoword)
                .addOnCompleteListener {
                    //get the result
                    if (!it.isSuccessful) return@addOnCompleteListener
                    //else if successfull
                    Log.d("Main", "successfully loggged in :${it.result.user.uid}")
                    uploadeImageToFireBaseStorage()
                }
                .addOnFailureListener {
                    Log.d("Main", "Failed to create user :${it.message}")
                    Toast.makeText(this, "Failed to create user ", Toast.LENGTH_SHORT).show()
                }
    }

    private fun uploadeImageToFireBaseStorage() {
    //save img to the folder /images, made in Firabase when register
        if (selectedPhotoIri == null) {
            Toast.makeText(this, "Please, upload image", Toast.LENGTH_SHORT).show()
            return
        }
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")//random long string

        ref.putFile(selectedPhotoIri!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        //save it also for the user
                        saveUseToFirebaseDB(it.toString())//it is the image
                    }
                }
                .addOnFailureListener {
                    //if fails
                    Log.d("Main", "No image :${it.message}")
                }
    }

    private fun saveUseToFirebaseDB(profileImgUrl: String) {
        //the user id from firebase
        val uid = FirebaseAuth.getInstance().uid ?: "" //if it is not empty string
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        //create user object and set it as value for the uid
        val user = User(uid, username_edittext.text.toString(), profileImgUrl, password_edtiitext.text.toString(), email_edittext.text.toString())
        ref.setValue(user)
                .addOnSuccessListener {
                    //we save the User in Firebase database
                    val intent = Intent(this, LatestMsgActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    //launch the Latestmsgactivity every time we create a new user - 1st screen
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d("Main", "user cannot be created :${it.message}")
                    Toast.makeText(this, "this user cannot be created ", Toast.LENGTH_SHORT).show()
                }
    }
}
