package com.example.ourproject.customer.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ourproject.Adapter.ProductAdapter
import com.example.ourproject.Adapter.CategoriesHomeAdapter
import com.example.ourproject.R
import com.example.ourproject.admin.fragments.DetailsProduct
import com.example.ourproject.modle.Products
import com.example.ourproject.modle.CategoriesHome
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main_nav.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_products.*

/**
 * A simple [Fragment] subclass.
 */


class HomeFragment : Fragment(), ProductAdapter.onProductsItemClickListener {

    lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        db = Firebase.firestore
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        activity!!.nav_view.visibility=View.VISIBLE

        getAllCategories()
        getBest()
        getTop()

        root.viewallCate.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.mainContainer,CategoryFragment()).commit()
            activity!!.nav_view.visibility=View.GONE
        }
        root.cart.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.mainContainer,MyPurchases()).addToBackStack(null).commit()
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
//        b.putString("plocation",data.location.toString())
        b.putBoolean("pisFavorite",data.isFavorite)
//        b.putString("pdescription",data.description)
//        b.putInt("pcountPurchase",data.countPurchase)
//        b.putString("pcategory",data.categoryName)

        fragment.arguments=b
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment).addToBackStack(null).commit()

        activity!!.nav_view.visibility=View.GONE
    }

    private fun getAllCategories(){
        val categoryList= mutableListOf<CategoriesHome>()
        db.collection("categories")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.e("hala", "${document.id} -> ${document.get("category_name")} -> ${document.get("category_image")}")
                        val id = document.id
                        val data = document.data
                        val categoryImage = data["category_image"] as String?
                        categoryList.add(CategoriesHome(categoryImage))
                    }



                    rv_categories.layoutManager = LinearLayoutManager(activity!!,RecyclerView.HORIZONTAL,false)
                    rv_categories.setHasFixedSize(true)


                    val categoriesHomeAdapter = CategoriesHomeAdapter(activity!!,categoryList)
                    rv_categories.adapter=categoriesHomeAdapter

                }
            }
    }



    private fun getBest(){
        val dataProduct = mutableListOf<Products>()

        db.collection("products").limit(6).orderBy("countPurchase",Query.Direction.DESCENDING)
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
                        val locationLat = data["locationLat"].toString()
                        val locationLng = data["locationLng"].toString()
                        val isFavorite = data["isFavorite"] as Boolean
                        val countPurchase = data["countPurchase"].toString().toInt()
                        val description = data["description"] as String?
                        val categoryName = data["categoryName"] as String?
                        val userRate = data["userRate"] as ArrayList<String>?
                        dataProduct.add(Products(id,image,name,price,rate,GeoPoint(locationLat.toDouble(),locationLng.toDouble()),isFavorite,countPurchase,description,categoryName))                    }

                    rv_best_seller.layoutManager = LinearLayoutManager(activity!!,RecyclerView.HORIZONTAL,false)
                    rv_best_seller.setHasFixedSize(true)

                    val productAdapter = ProductAdapter(activity!!,dataProduct,this)
                    rv_best_seller.adapter=productAdapter

                }
            }
    }


    private fun getTop(){
        val dataProduct = mutableListOf<Products>()
        db.collection("products").limit(6).orderBy("rate",Query.Direction.DESCENDING)
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
                        val locationLat = data["locationLat"].toString()
                        val locationLng = data["locationLng"].toString()
                        val isFavorite = data["isFavorite"] as Boolean
                        val countPurchase = data["countPurchase"].toString().toInt()
                        val description = data["description"] as String?
                        val categoryName = data["categoryName"] as String?
                        val userRate = data["userRate"] as ArrayList<String>?
                        dataProduct.add(Products(id,image,name,price,rate,GeoPoint(locationLat.toDouble(),locationLng.toDouble()),isFavorite,countPurchase,description,categoryName))
                    }

                    rv_top_rated.layoutManager = GridLayoutManager(activity!!,2)
                    rv_top_rated.setHasFixedSize(true)

                    val productAdapter2 = ProductAdapter(activity!!,dataProduct,this)
                    rv_top_rated.adapter=productAdapter2

                }
            }
    }


}