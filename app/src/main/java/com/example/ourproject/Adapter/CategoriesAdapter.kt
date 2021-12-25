package com.example.ourproject.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourproject.R
import com.example.ourproject.modle.Category
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.categories_item.view.*
import kotlinx.android.synthetic.main.fragment_products.view.*

class CategoriesAdapter(var activity: Context?, var data: MutableList<Category>, var clickListener: onCategoryItemClickListener): RecyclerView.Adapter<CategoriesAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageCategory  =itemView.categoryImage
        val nameCategory=itemView.categoryName

        fun initialize(data: Category, action:onCategoryItemClickListener){
           // imageCategory.setImageURI(Uri.parse(data.imageCategory))
            Picasso.get().load(data.imageCategory).into(imageCategory)

            nameCategory.text = data.nameCategory

            itemView.setOnClickListener {
                action.onItemClick(data,adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesAdapter.MyViewHolder {
        var View: View = LayoutInflater.from(activity).inflate(R.layout.categories_item,parent,false)
        val myHolder:MyViewHolder = MyViewHolder(View)
        return myHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        holder.imageCategory!!.setImageResource(data[position].imageCategory)
//        holder.nameCategory?.text = data[position].nameCategory
        holder.initialize(data.get(position), clickListener)
    }
    interface onCategoryItemClickListener{
        fun onItemClick(data:Category, position: Int)
    }
}