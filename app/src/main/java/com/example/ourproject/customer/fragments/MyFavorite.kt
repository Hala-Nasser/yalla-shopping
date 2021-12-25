package com.example.ourproject.customer.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ourproject.Adapter.ProductAdapter

import com.example.ourproject.R
import com.example.ourproject.modle.Products
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main_nav.*
import kotlinx.android.synthetic.main.fragment_my_favorite.*
import kotlinx.android.synthetic.main.fragment_my_favorite.view.*

/**
 * A simple [Fragment] subclass.
 */
class MyFavorite : Fragment(), ProductAdapter.onProductsItemClickListener {

    lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_my_favorite, container, false)

        db = Firebase.firestore
        getProductsFavorite()
         root.back_to_profile.setOnClickListener {
             activity!!.supportFragmentManager.beginTransaction().replace(R.id.mainContainer,Profile()).commit()
             activity!!.nav_view.visibility=View.VISIBLE
         }

        return root
    }

    override fun onItemClick(data: Products, position: Int) {
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.mainContainer,ProductDetailsUser()).addToBackStack(null).commit()
        activity!!.nav_view.visibility=View.GONE
    }
    private fun getProductsFavorite(){
        val dataProduct = mutableListOf<Products>()

        db.collection("products").whereEqualTo("isFavorite",true)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.e("hala_product", "${document.id} -> ${document.get("name")} -> ${document.get("image")}")
                        val id = document.id
                        val data = document.data
                        val name = data["name"] as String?
                        val image = data["image"] as String?
                        val price = data["price"] as Double
                        val rate = data["rate"] as Double
                        val locationLat = data["locationLat"].toString().toDouble()
                        val locationLng = data["locationLng"].toString().toDouble()
                        val isFavorite = data["isFavorite"] as Boolean
                        val countPurchase = data["countPurchase"].toString().toInt()
                        val description = data["description"] as String?
                        val categoryName = data["categoryName"] as String?
                        val userRate = data["userRate"] as ArrayList<String>?
                        dataProduct.add(Products(id,image,name,price,rate,
                            GeoPoint(locationLat,locationLng),isFavorite,countPurchase,description,categoryName))
                    }

                    rv_my_favorite.layoutManager = GridLayoutManager(activity!!, 2)
                    rv_my_favorite.setHasFixedSize(true)
                    val categoriesHomeAdapter = ProductAdapter(activity!!, dataProduct,this)
                    rv_my_favorite.adapter = categoriesHomeAdapter

                }
            }
    }

}
