package com.example.ourproject.customer.fragments


import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ourproject.Adapter.ProductAdapter
import com.example.ourproject.R
import com.example.ourproject.modle.Products
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main_nav.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*


/**
 * A simple [Fragment] subclass.
 */
class Search : Fragment() , ProductAdapter.onProductsItemClickListener{


    lateinit var db: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        db= Firebase.firestore

        val root=inflater.inflate(R.layout.fragment_search, container, false)
        activity!!.nav_view.visibility=View.VISIBLE

        root.search.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                root.image_search.visibility=View.GONE
                getProductsSearch(newText)
                return true
            }

        })


        return root
    }

    override fun onItemClick(data: Products, position: Int) {
        val fragment =   ProductDetailsUser()
        val b=Bundle()
        b.putString("pid",data.id)
        b.putString("pname",data.name)
        b.putString("pimage",data.image)
        b.putDouble("pprice",data.price)
        b.putDouble("prate",data.rate)
        b.putString("plocation",data.location.toString())
        b.putBoolean("pisFavorite",data.isFavorite)
        b.putString("pdescription",data.description)
        b.putInt("pcountPurchase",data.countPurchase)
        b.putString("pcategory",data.categoryName)

        fragment.arguments=b
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment).addToBackStack(null).commit()
        activity!!.nav_view.visibility=View.GONE
    }

    private fun getProductsSearch(search_stat:String){
        val dataProduct = mutableListOf<Products>()

        db.collection("products")
            .orderBy("name").startAt(search_stat).endAt(search_stat+"\uf8ff")
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
                        rv_search.layoutManager = GridLayoutManager(activity!!, 2)
                        rv_search.setHasFixedSize(true)

                        val productAdapter = ProductAdapter(activity!!, dataProduct, this)
                        rv_search.adapter = productAdapter
                    }

                }
            }


    }


