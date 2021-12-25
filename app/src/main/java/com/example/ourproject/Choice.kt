package com.example.ourproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ourproject.admin.LogInAdmin
import com.example.ourproject.customer.LogIn
import com.example.ourproject.customer.SingUp
import kotlinx.android.synthetic.main.activity_choice.*

class Choice : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choice)

        admin.setOnClickListener {
            val i = Intent(this, LogInAdmin::class.java)
            startActivity(i)
        }

        customer.setOnClickListener {
            val i = Intent(this, SingUp::class.java)
            startActivity(i)
        }

    }
}
