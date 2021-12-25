package com.example.ourproject.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourproject.R
import com.example.ourproject.modle.CategoriesHome
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.categoeies_home_item.view.*


class CategoriesHomeAdapter(var activity: Context?, var data :MutableList<CategoriesHome>) : RecyclerView.Adapter<CategoriesHomeAdapter.MyViewHolder>(){
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val photo  =itemView.categories_home_photo

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesHomeAdapter.MyViewHolder {
        var View:View= LayoutInflater.from(activity).inflate(R.layout.categoeies_home_item ,parent ,false)
        val myHolder:MyViewHolder = MyViewHolder(View)
        return myHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CategoriesHomeAdapter.MyViewHolder, position: Int) {
       // holder.photo.setImageResource(data[position].photo)
        Picasso.get().load(data[position].photo).into(holder.photo)
    }
}