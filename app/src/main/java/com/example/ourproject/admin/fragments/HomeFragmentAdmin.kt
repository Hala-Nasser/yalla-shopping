package com.example.ourproject.admin.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ourproject.Adapter.ProductAdapter
import com.example.ourproject.Choice

import com.example.ourproject.R
import com.example.ourproject.admin.LogInAdmin
import com.example.ourproject.modle.Products
import kotlinx.android.synthetic.main.activity_main_admin.*
import kotlinx.android.synthetic.main.activity_main_admin.view.*
import kotlinx.android.synthetic.main.fragment_home_admin.*
import kotlinx.android.synthetic.main.fragment_home_admin.view.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragmentAdmin : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home_admin, container, false)
        root.Most_requested_cardview.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainerAdmin, MostRequested()).addToBackStack(null).commit()
        }
        root.Top_rated_cardview.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainerAdmin, TopRated()).addToBackStack(null).commit()
        }
        root.Products_purchased_cardview.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainerAdmin, ProductsPurchased()).addToBackStack(null).commit()
        }

        root.Categories_cardview.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainerAdmin, CategoryFragmentAdmin()).addToBackStack(null).addToBackStack(null)
                .commit()
        }
        root.Add_product_cardview.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainerAdmin, AddProduct()).addToBackStack(null).commit()
        }
        root.Logout_cardview.setOnClickListener {

            val alertDialog = AlertDialog.Builder(activity!!)
            alertDialog.setTitle("Log out ")
            alertDialog.setMessage("Are you sure?")
            alertDialog.setCancelable(false)
            alertDialog.setIcon(R.drawable.logout_black)

            alertDialog.setPositiveButton("Yes") { dialogInterface, i ->
                val i = Intent(activity, Choice::class.java)
                startActivity(i)
                activity!!.finish()
            }
            alertDialog.setNegativeButton("No") { dialogInterface, i ->
                activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.mainContainerAdmin, HomeFragmentAdmin()).commit()
            }
            alertDialog.create().show()
        }




        return root
    }

}
