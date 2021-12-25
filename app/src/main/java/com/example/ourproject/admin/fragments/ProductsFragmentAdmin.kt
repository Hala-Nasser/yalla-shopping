


package com.example.ourproject.admin.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ourproject.Adapter.ProductAdapter
import com.example.ourproject.Adapter.ProductAdminAdapter
import com.example.ourproject.R
import com.example.ourproject.modle.Products
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_products_admin.*
import kotlinx.android.synthetic.main.fragment_products_admin.view.*
import kotlinx.android.synthetic.main.fragment_products_admin.view.category_back
import kotlinx.android.synthetic.main.fragment_products_admin.view.txt_category_name

/**
 * A simple [Fragment] subclass.
 */
class ProductsFragmentAdmin : Fragment(), ProductAdminAdapter.onProductsItemClickListener {

    lateinit var db: FirebaseFirestore
    var catImage:String?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_products_admin, container, false)


        db = Firebase.firestore
        val b = arguments
        if (b != null) {
            val catId =b.getString("id")
            val catImage=b.getString("catImage")
            val catName=b.getString("catName")
            Picasso.get().load(catImage).into(root.category_image)
            root.txt_category_name.text=catName
//            Toast.makeText(activity!!,"$catName", Toast.LENGTH_LONG).show()
            getProductsAccordingToCategory("$catName")
        }

        val catId =b?.getString("id")
        catImage=b?.getString("catImage")
        val catName=b?.getString("catName")

        root.category_back.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainerAdmin, CategoryFragmentAdmin()).commit()
        }

        root.more.setOnClickListener {
            val popup = PopupMenu(activity!!, more)
            popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.edit -> {
                        val fragment = EditCategory()
                        val b=Bundle()
                        b.putString("id",catId)
                        b.putString("catImage",catImage)
                        b.putString("catName",catName)
                        fragment.arguments=b
                        activity!!.supportFragmentManager.beginTransaction()
                            .replace(R.id.mainContainerAdmin, fragment).commit()
                    }
                    R.id.delete ->{
                        val alertDialog = AlertDialog.Builder(activity!!)
                        alertDialog.setTitle("Delete product")
                        alertDialog.setMessage("Are you sure?")
                        alertDialog.setCancelable(false)
                        alertDialog.setIcon(R.drawable.ic_delete)

                        alertDialog.setPositiveButton("Yes") { dialogInterface, i ->
                            deleteCat(catId!!)
                            // Snackbar.make(products_layout,"Deletion successful", Snackbar.LENGTH_LONG).show()
                            activity!!.supportFragmentManager.beginTransaction()
                                .replace(R.id.mainContainerAdmin, CategoryFragmentAdmin()).commit()
                        }
                        alertDialog.setNegativeButton("No") { dialogInterface, i ->
//                            activity!!.supportFragmentManager.beginTransaction()
//                                .replace(R.id.mainContainerAdmin, ProductsFragmentAdmin()).commit()
                        }
                        alertDialog.create().show()
                    }

                }
                true
            }
            popup.show()
        }


        return root
    }

    override fun onItemClick(data: Products, position: Int) {
        val fragment =   DetailsProduct()
        val b=Bundle()
        b.putString("pid",data.id)
        b.putString("pname",data.name)
        b.putString("pimage",data.image)
        b.putDouble("pprice",data.price)
        b.putDouble("prate",data.rate)
        b.putDouble("plat",data.location.latitude)
        b.putDouble("plong",data.location.longitude)
        b.putBoolean("pisFavorite",data.isFavorite)
        b.putString("pdescription",data.description)
        b.putInt("pcountPurchase",data.countPurchase)
        b.putString("pcategory",data.categoryName)

        fragment.arguments=b
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainerAdmin, fragment).addToBackStack(null).commit()
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
                        val locationLat = data["locationLat"].toString().toDouble()
                        val locationLng = data["locationLng"].toString().toDouble()
                        val isFavorite = data["isFavorite"] as Boolean
                        val countPurchase = data["countPurchase"].toString().toInt()
                        val description = data["description"] as String?
                        val categoryName = data["categoryName"] as String?
                        val userRate = data["userRate"] as ArrayList<String>?
                        dataProduct.add(Products(id,image,name,price,rate,GeoPoint(locationLat,locationLng),isFavorite,countPurchase,description,categoryName))
                    }

                    rv_product.layoutManager = GridLayoutManager(activity!!, 2)
                    rv_product.setHasFixedSize(true)
                    val productAdapter = ProductAdminAdapter(activity!!, dataProduct,this)
                    rv_product.adapter = productAdapter

                }
            }
    }
    private fun deleteCat(id:String){
        db.collection("categories").document(id).delete()
            .addOnSuccessListener {
                //      Snackbar.make(products_layout,"category deleted successful",Snackbar.LENGTH_SHORT).show()
            }
    }
}
