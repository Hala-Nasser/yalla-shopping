package com.example.ourproject.customer.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ourproject.Adapter.CategoriesAdapter
import com.example.ourproject.R
import com.example.ourproject.modle.Category
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main_nav.*
import kotlinx.android.synthetic.main.activity_main_nav.view.*
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.fragment_category.view.*

/**
 * A simple [Fragment] subclass.
 */
class CategoryFragment : Fragment(), CategoriesAdapter.onCategoryItemClickListener {

    lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        db = Firebase.firestore
        val root= inflater.inflate(R.layout.fragment_category, container, false)
        getAllCategories()
        root.category_back.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.mainContainer,HomeFragment()).addToBackStack(null).commit()
            activity!!.nav_view.visibility=View.VISIBLE
        }

        return root
    }

    override fun onItemClick(data: Category, position: Int) {
        val fragment =   ProductsFragment()
        val b=Bundle()
        b.putString("id",data.id)
        b.putString("catImage",data.imageCategory)
        b.putString("catName",data.nameCategory)
        fragment.arguments=b
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.mainContainer,fragment).addToBackStack(null).commit()
        activity!!.nav_view.visibility=View.GONE
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


                    rv_category.layoutManager = LinearLayoutManager(
                        activity!!,
                        RecyclerView.VERTICAL, false
                    )
                    rv_category.setHasFixedSize(true)


                    val categoriesHomeAdapter = CategoriesAdapter(activity!!, categoryList,this)
                    rv_category.adapter = categoriesHomeAdapter
                }
            }
    }

}
