package com.example.ourproject.admin.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ourproject.Adapter.CategoriesAdapter
import com.example.ourproject.Adapter.ProductAdapter

import com.example.ourproject.R
import com.example.ourproject.customer.fragments.ProductsFragment
import com.example.ourproject.modle.Category
import com.example.ourproject.modle.Products
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_category_admin.*
import kotlinx.android.synthetic.main.fragment_category_admin.view.*

/**
 * A simple [Fragment] subclass.
 */
    class CategoryFragmentAdmin : Fragment(), CategoriesAdapter.onCategoryItemClickListener {

    lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = Firebase.firestore

        val root = inflater.inflate(R.layout.fragment_category_admin, container, false)



        getAllCategories()
        root.back_dashboard.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainerAdmin, HomeFragmentAdmin()).commit()
        }

        root.add_category.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.mainContainerAdmin,AddCategory()).commit()
        }

        return root
    }

    override fun onItemClick(data: Category, position: Int) {
        val fragment =   ProductsFragmentAdmin()
        val b=Bundle()
        b.putString("id",data.id)
        b.putString("catImage",data.imageCategory)
        b.putString("catName",data.nameCategory)
        fragment.arguments=b
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.mainContainerAdmin,fragment).addToBackStack(null).commit()
    }

    private fun getAllCategories(){
        val categoryList= mutableListOf<Category>()
        db.collection("categories")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.e("hala", "${document.id} -> ${document.get("category_name")} -> ${document.get("category_image")}")
                        val id = document.id
                        val data = document.data
                        val categoryName = data["category_name"] as String?
                        val categoryImage = data["category_image"] as String?
                        categoryList.add(Category(id, categoryImage, categoryName))
                    }
                    rv_category_add_admain.layoutManager =
                        LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
                    rv_category_add_admain.setHasFixedSize(true)
                    val categoriesAdapter = CategoriesAdapter(activity!!, categoryList, this)
                    rv_category_add_admain.adapter = categoriesAdapter
                }
            }
    }
}
