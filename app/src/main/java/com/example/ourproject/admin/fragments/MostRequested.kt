package com.example.ourproject.admin.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ourproject.Adapter.ProductAdapter
import com.example.ourproject.Adapter.ProductAdminAdapter

import com.example.ourproject.R
import com.example.ourproject.modle.Products
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_most_requested.*
import kotlinx.android.synthetic.main.fragment_most_requested.view.*
import kotlinx.android.synthetic.main.fragment_products_purchased.*

/**
 * A simple [Fragment] subclass.
 */
class MostRequested : Fragment(), ProductAdminAdapter.onProductsItemClickListener {
    lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = Firebase.firestore
        val root= inflater.inflate(R.layout.fragment_most_requested, container, false)

        root.back_dashboard.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainerAdmin, HomeFragmentAdmin()).commit()
        }
        getProductsMostRequested()
        return root
    }

    override fun onItemClick(data: Products, position: Int) {
        //todo
    }

    private fun getProductsMostRequested(){
        val dataProduct = mutableListOf<Products>()

        db.collection("products").orderBy("countPurchase",Query.Direction.DESCENDING)
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

                    rv_most_requested.layoutManager = GridLayoutManager(activity!!, 2)
                    rv_most_requested.setHasFixedSize(true)
                    val categoriesHomeAdapter2 = ProductAdminAdapter(activity!!, dataProduct,this)
                    rv_most_requested.adapter = categoriesHomeAdapter2

                }
            }
    }

}
