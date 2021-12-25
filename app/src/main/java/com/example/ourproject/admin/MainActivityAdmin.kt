package com.example.ourproject.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.ourproject.R
import com.example.ourproject.admin.fragments.*
import com.example.ourproject.customer.fragments.CategoryFragment
import com.example.ourproject.customer.fragments.HomeFragment
import kotlinx.android.synthetic.main.activity_main_admin.*

class MainActivityAdmin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)

        supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainerAdmin, HomeFragmentAdmin()).addToBackStack(null)
            .commit()


    }
}
