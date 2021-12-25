package com.example.ourproject.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourproject.modle.Products
import com.example.ourproject.R
import com.example.ourproject.admin.fragments.DetailsProduct
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.products_item.view.*


class ProductAdapter(var activity: Context?, var data: MutableList<Products>, var clickListener: onProductsItemClickListener) : RecyclerView.Adapter<ProductAdapter.MyViewHolder>(){

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val db= Firebase.firestore
        val productImage  =itemView.product_photo
        val name=itemView.product_name
        val price=itemView.product_price
        val favorite=itemView.favorite

        fun initialize(data: Products, action:onProductsItemClickListener){

           // productImage.setImageURI(Uri.parse(data.image))
            Picasso.get().load(data.image).into(productImage)
            name.text = data.name
            price.text = data.price.toString()


            if (data.isFavorite==true){
                favorite.setImageResource(R.drawable.ic_favorite_black_24dp)
            }else{
                favorite.setImageResource(R.drawable.ic_favorite_border)
            }

            favorite.setOnClickListener {
                if (data.isFavorite==true){
                    updateFavorite(data.id!!,false)
                    data.isFavorite=false
                    favorite.setImageResource(R.drawable.ic_favorite_border)
                }else{
                    updateFavorite(data.id!!,true)
                   data.isFavorite=true
                    favorite.setImageResource(R.drawable.ic_favorite_black_24dp)
                }
            }

            itemView.setOnClickListener {
                action.onItemClick(data,adapterPosition)
            }
        }

        private fun updateFavorite(id:String, isFavorite:Boolean){
            val product=HashMap<String,Any>()
            product["isFavorite"]=isFavorite
            db.collection("products").document(id)
                .update(product)
                .addOnSuccessListener { querySnapshot ->
                    Log.e("hala","updated favorite")
                }.addOnFailureListener { exception ->
                    Log.e("hala","failed updated favorite")
                }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapter.MyViewHolder {
        var View: View = LayoutInflater.from(activity).inflate(R.layout.products_item,parent,false)
        val myHolder:MyViewHolder = MyViewHolder(View)
        return myHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ProductAdapter.MyViewHolder, position: Int) {
//        holder.productImage!!.setImageResource(data[position].image)
//        holder.name?.text = data[position].name
//        holder.price?.text = data[position].price
        holder.initialize(data.get(position), clickListener)
    }
    interface onProductsItemClickListener{
        fun onItemClick(data:Products, position: Int)
    }


}