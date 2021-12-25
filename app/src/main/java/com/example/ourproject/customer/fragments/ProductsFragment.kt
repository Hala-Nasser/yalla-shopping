//package com.example.ourproject.customer.fragments
//
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.ourproject.Adapter.CategoriesAdapter
//import com.example.ourproject.Adapter.ProductAdapter
//
//import com.example.ourproject.R
//import com.example.ourproject.modle.Category
//import com.example.ourproject.modle.Products
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.GeoPoint
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import com.squareup.picasso.Picasso
//import kotlinx.android.synthetic.main.activity_main_nav.*
//import kotlinx.android.synthetic.main.fragment_category.*
//import kotlinx.android.synthetic.main.fragment_products.*
//import kotlinx.android.synthetic.main.fragment_products.view.*
//
//class ProductsFragment : Fragment(), ProductAdapter.onProductsItemClickListener {
//
//    lateinit var db: FirebaseFirestore
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        db = Firebase.firestore
//
//        val root = inflater.inflate(R.layout.fragment_products, container, false)
//
//
//        val b = arguments
//        if (b != null) {
//            val catId =b.getString("id")
//            val catImage=b.getString("catImage")
//           val catName=b.getString("catName")
//
//            //root.category_photo.setImageURI(Uri.parse(catImage))
//            Picasso.get().load(catImage).into(root.category_photo)
//            root.txt_category_name.text=catName
//            getProductsAccordingToCategory("$catName")
//        }
//
//
//
//        root.category_back.setOnClickListener {
//            activity!!.supportFragmentManager.beginTransaction().replace(R.id.mainContainer,CategoryFragment()).commit()
//            activity!!.nav_view.visibility=View.GONE
//        }
//
//
//        return root
//    }
//    override fun onItemClick(data: Products, position: Int) {
//        val fragment =   ProductDetailsUser()
//        val b=Bundle()
//        b.putString("pid",data.id)
//        b.putString("pname",data.name)
//        b.putString("pimage",data.image)
//        b.putDouble("pprice",data.price)
//        b.putDouble("prate",data.rate)
//        //b.put("plocation",data.id)
//        b.putBoolean("pisFavorite",data.isFavorite)
//        b.putString("pdescription",data.description)
//        b.putInt("pcountPurchase",data.countPurchase)
//
//        fragment.arguments=b
//
//        activity!!.supportFragmentManager.beginTransaction().replace(R.id.mainContainer,fragment).addToBackStack(null).commit()
//        activity!!.nav_view.visibility=View.GONE
//    }
//
//    private fun getProductsAccordingToCategory(catName:String){
//        val dataProduct = mutableListOf<Products>()
//
//        db.collection("products").whereEqualTo("categoryName",catName)
//            .get()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    for (document in task.result!!) {
//                        Log.e("hala_product", "${document.id} -> ${document.get("name")} -> ${document.get("image")}")
//                        val id = document.id
//                        val data = document.data
//                        val name = data["name"] as String?
//                        val image = data["image"] as String?
//                        val price = data["price"] as Double
//                        val rate = data["rate"] as Double
//                        val location = data["location"] as GeoPoint
//                        val isFavorite = data["isFavorite"] as Boolean
//                        val countPurchase = data["countPurchase"].toString().toInt()
//                        val description = data["description"] as String?
//                        val categoryName = data["categoryName"] as String?
//                        dataProduct.add(Products(id,image,name,price,rate,location,isFavorite,countPurchase,description,categoryName))
//                    }
//
//                    rv_product_category.layoutManager = GridLayoutManager(activity!!,2)
//                    rv_product_category.setHasFixedSize(true)
//
//                    val productAdapter = ProductAdapter(activity!!, dataProduct,this)
//                    rv_product_category.adapter = productAdapter
//                }
//            }
//    }
//
//}



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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main_nav.*
import kotlinx.android.synthetic.main.fragment_products.*
import kotlinx.android.synthetic.main.fragment_products.view.*

class ProductsFragment : Fragment(), ProductAdapter.onProductsItemClickListener {

    lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = Firebase.firestore

        val root = inflater.inflate(R.layout.fragment_products, container, false)


        val b = arguments
        if (b != null) {
            val catId =b.getString("id")
            val catImage=b.getString("catImage")
            val catName=b.getString("catName")

            //root.category_photo.setImageURI(Uri.parse(catImage))
            Picasso.get().load(catImage).into(root.category_photo)
            root.txt_category_name.text=catName
            getProductsAccordingToCategory("$catName")
        }



        root.category_back.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.mainContainer,CategoryFragment()).commit()
            activity!!.nav_view.visibility=View.GONE
        }


        return root
    }
    override fun onItemClick(data: Products, position: Int) {
        val fragment =   ProductDetailsUser()
        val b=Bundle()
        b.putString("pid",data.id)
//        b.putString("pname",data.name)
//        b.putString("pimage",data.image)
//        b.putDouble("pprice",data.price)
        b.putDouble("prate",data.rate)
        b.putDouble("plat",data.location.latitude)
        b.putDouble("plong",data.location.longitude)
        b.putBoolean("pisFavorite",data.isFavorite)
//        b.putString("pdescription",data.description)
//        b.putInt("pcountPurchase",data.countPurchase)

        fragment.arguments=b

        activity!!.supportFragmentManager.beginTransaction().replace(R.id.mainContainer,fragment).addToBackStack(null).commit()
        activity!!.nav_view.visibility=View.GONE
    }

    private fun getProductsAccordingToCategory(catName:String){
        val dataProduct = mutableListOf<Products>()

        db.collection("products").whereEqualTo("categoryName",catName)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.e("hala_product", "${document.id} -> ${document.get("name")} -> ${document.get("locationLat")}")
                        val id = document.id
                        val data = document.data
                        val name = data["name"] as String?
                        val image = data["image"] as String?
                        val price = data["price"] as Double
                        val rate = data["rate"] as Double
                        val locationLat = data["locationLat"].toString()
                        val locationLng = data["locationLng"].toString()
                        val isFavorite = data["isFavorite"] as Boolean
                        val countPurchase = data["countPurchase"].toString().toInt()
                        val description = data["description"] as String?
                        val categoryName = data["categoryName"] as String?
                        val userRate = data["userRate"] as ArrayList<String>?
                        dataProduct.add(Products(id,image,name,price,rate,GeoPoint(locationLat.toDouble(),locationLng.toDouble()),isFavorite,countPurchase,description,categoryName))
                    }

                    rv_product_category.layoutManager = GridLayoutManager(activity!!,2)
                    rv_product_category.setHasFixedSize(true)

                    val productAdapter = ProductAdapter(activity!!, dataProduct,this)
                    rv_product_category.adapter = productAdapter
                }
            }
    }

}