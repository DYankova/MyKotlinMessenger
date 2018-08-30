package com.example.dilyanayankova.my_messenger.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.dilyanayankova.my_messenger.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.auth.AuthResult
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener

class LoginActivity : AppCompatActivity() {
    companion object { //add static constants!
        val LOGGEDKEY = "LOGGEDKEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login.setOnClickListener {
            val email = email.text.toString()
            val passoword = password.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, passoword)
                    .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                        if (task.isSuccessful) {
                            val logggedUser = FirebaseAuth.getInstance().getCurrentUser()
                            val intent = Intent(this, LatestMsgActivity::class.java)
                            intent.putExtra(LOGGEDKEY, logggedUser)
                            startActivity(intent)
                        } else {
                            Log.w("signInWithEmail:failure", task.exception)

                        }
                    })
        }
        back_to_reg.setOnClickListener {
            //launch the login activity
            val intent = Intent(this, RegisterActivity::class.java) //represent the class
            startActivity(intent)
        }
    }
}