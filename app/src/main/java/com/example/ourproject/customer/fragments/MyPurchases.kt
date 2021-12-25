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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main_nav.*
import kotlinx.android.synthetic.main.fragment_my_purchases.*
import kotlinx.android.synthetic.main.fragment_my_purchases.view.*
import kotlinx.android.synthetic.main.fragment_products_purchased.*

/**
 * A simple [Fragment] subclass.
 */
class MyPurchases : Fragment(), ProductAdapter.onProductsItemClickListener {

    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var myPurch:ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_my_purchases, container, false)

        db = Firebase.firestore
        auth= Firebase.auth
        getProductsPurchased()
        Log.e("getProductsPurchased",getProductsPurchased().toString())
        root.back_to_profile.setOnClickListener {
            fragmentManager?.popBackStack()
            activity!!.nav_view.visibility=View.VISIBLE

        }
        return root
    }

    override fun onItemClick(data: Products, position: Int) {
        //todo
    }

    private fun getProductsPurchased(){
        val dataProduct = mutableListOf<Products>()

        db.collection("products").whereGreaterThanOrEqualTo ("countPurchase",1)
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
                        dataProduct.add(Products(id,image,name,price,rate,GeoPoint(locationLat,locationLng),isFavorite,countPurchase,description,categoryName))
                    }

                    rv_my_purchases.layoutManager = GridLayoutManager(activity!!, 2)
                    rv_my_purchases.setHasFixedSize(true)
                    val categoriesHomeAdapter = ProductAdapter(activity!!, dataProduct,this)
                    rv_my_purchases.adapter = categoriesHomeAdapter

                }
            }
    }

}
