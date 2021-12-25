package com.example.ourproject.customer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.ourproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sing_up.*

class SingUp : AppCompatActivity() {

    val TAG = "3la"
    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null
    var myPurchase=ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)

        auth = Firebase.auth
        db = Firebase.firestore

        login.setOnClickListener {
            val i = Intent(this, LogIn::class.java)
            startActivity(i)
        }
        btn_signup.setOnClickListener {
            createNewAccount(email.text.toString(), password.text.toString())
        }


    }

    private fun createNewAccount(email: String, pass: String) {
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                Log.e("TestAuthn", "user ${user!!.uid} + ${user.email}")
                addUser(
                    user.uid,
                    username.text.toString(),
                    user.email!!,
                    password.text.toString(),
                    myPurchase
                )
                var i =Intent(this,MainActivity_nav::class.java)
                startActivity(i)

            } else {
                Toast.makeText(this, "sing up failed", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun addUser( id: String, username: String, email: String, password: String, myPurshase:ArrayList<String>) {

        val user = hashMapOf("id" to id,"username" to username, "email" to email, "password" to password, "myPurchase" to myPurshase)

        db!!.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.e(TAG, "User added Successfully with user id ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.message)
            }
    }



}