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
import kotlinx.android.synthetic.main.activity_log_in.*

class LogIn : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    var db: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        auth = Firebase.auth
        db = Firebase.firestore
        btn_login.setOnClickListener {
            LogInAccount(email.text.toString(),password.text.toString())

        }
        signup.setOnClickListener {
            val i = Intent(this, SingUp::class.java)
            startActivity(i)
        }
    }

    private fun LogInAccount(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                Log.e("log in", "user ${user!!.uid} + ${user.email}")
                val i = Intent(this, MainActivity_nav::class.java)
                startActivity(i)

            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }




//    private fun checkuserLogin(username: String, password: String) {
//        var isexit = false
//        db.collection("users")
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                for (document in querySnapshot) {
//                    if (username.equals(document.getString("username")) && password.equals(
//                            document.getString(
//                                "password"
//                            )
//                        )
//                    ) {
//
//                        isexit = true
//                        break
//                    } else {
//                        isexit = false
//                    }
//                }
//
//                if (isexit == true) {
//                    val i = Intent(this, MainActivity_nav::class.java)
//                    startActivity(i)
//                } else {
//                    Toast.makeText(this, "username or password wrong", Toast.LENGTH_LONG).show()
//                }
//
//            }
//            .addOnFailureListener { exception ->
//                Log.e(TAG, exception.message)
//            }
//    }

}
