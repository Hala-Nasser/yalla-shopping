package com.example.ourproject.customer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.ourproject.R
import kotlinx.android.synthetic.main.activity_main_nav.*
import kotlinx.android.synthetic.main.fragment_setting.view.*

/**
 * A simple [Fragment] subclass.
 */
class SettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_setting, container, false)
        root.back_to_profile.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.mainContainer,Profile()).commit()
            activity!!.nav_view.visibility=View.VISIBLE

        }
        return root
    }

}
