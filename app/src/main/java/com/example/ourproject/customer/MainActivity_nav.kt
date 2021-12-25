package com.example.ourproject.customer

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.ourproject.R
import com.example.ourproject.customer.fragments.HomeFragment
import com.example.ourproject.customer.fragments.Profile
import com.example.ourproject.customer.fragments.Search
import kotlinx.android.synthetic.main.activity_main_nav.*

class MainActivity_nav : AppCompatActivity() {
    companion object{
        private const val HOME=1
        private const val SEARCH=2
        private const val PROFILE=3

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_nav)

        nav_view.show(HOME)
        supportFragmentManager.beginTransaction().replace(
            R.id.mainContainer,
            HomeFragment()
        ).addToBackStack(null).commit()

        nav_view.add(
            MeowBottomNavigation.Model(
                HOME,
                R.drawable.ic_home
            ))
        nav_view.add(
            MeowBottomNavigation.Model(
                SEARCH,
                R.drawable.ic_search
            ))
        nav_view.add(
            MeowBottomNavigation.Model(
                PROFILE,
                R.drawable.ic_profile
            ))



        nav_view.setOnClickMenuListener {
            var fragment: Fragment?=null
            when(it.id){
               HOME -> fragment= HomeFragment()
               SEARCH -> fragment= Search()
               PROFILE ->fragment= Profile()
            }
            if (fragment!=null){
                val fragmentMang= supportFragmentManager
                fragmentMang.beginTransaction().replace(
                    R.id.mainContainer,
                    fragment).commit()
            }
        }


    }

}
