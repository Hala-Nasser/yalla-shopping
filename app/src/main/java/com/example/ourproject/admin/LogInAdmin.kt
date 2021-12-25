package com.example.ourproject.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.ourproject.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_log_in_admin.*

class LogInAdmin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in_admin)

        btnLogin.setOnClickListener {
            var password= password.text.toString()
            var username=username.text.toString()

            if (username.equals("admin")&& password.equals("123456")){
                val i = Intent(this, MainActivityAdmin::class.java)
                startActivity(i)
                finish()
            }else{
                Snackbar.make(login, "Username or password is incorrect, Please try again", Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
