
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

import com.example.ourproject.R
import com.example.ourproject.customer.fragments.HomeFragment
import com.example.ourproject.modle.Products
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_details_product.*
import kotlinx.android.synthetic.main.fragment_details_product.more
import kotlinx.android.synthetic.main.fragment_details_product.view.*
import kotlinx.android.synthetic.main.fragment_products_admin.*

/**
 * A simple [Fragment] subclass.
 */
class DetailsProduct : Fragment()  , OnMapReadyCallback {

    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    var markerB=true
    lateinit var mMap: GoogleMap
    // latitude location
    var lat= 0.toDouble()
    // long location
    var lng=0.toDouble()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root= inflater.inflate(R.layout.fragment_details_product, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.Addproduct_location) as SupportMapFragment
        mapFragment.getMapAsync(this)

        db= Firebase.firestore
        auth = Firebase.auth

        val b = arguments
        if (b != null) {
            val pid = b.getString("pid")
            val pname = b.getString("pname")
            val pimage = b.getString("pimage")
            val pprice = b.getDouble("pprice")
            val prate = b.getDouble("prate")
            lat=b.getDouble("plat")
            lng=b.getDouble("plong")
            var pisFavorite = b.getBoolean("pisFavorite")
            val pdescription = b.getString("pdescription")
            var pcountPurchase=b.getInt("pcountPurchase")
            val pcategory=b.getString("pcategory")

            Picasso.get().load(pimage).into(root.product_image)
            root.product_name.text = pname
            root.product_price.text = pprice.toString()
            root.rat_bar.rating = prate.toFloat()
            root.product_desc.text = pdescription
            root.rat_bar.rating=prate.toFloat()


            root.more.setOnClickListener {
                val popup = PopupMenu(activity!!, more)
                popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.edit -> {
                            val fragment = EditProductDetails()
                            val b = Bundle()
                            b.putString("pid", pid)
                            b.putString("pcategory", pcategory)

                            fragment.arguments = b
                            activity!!.supportFragmentManager.beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.mainContainerAdmin, fragment).commit()
                        }
                        R.id.delete -> {
                            val alertDialog = AlertDialog.Builder(activity!!)
                            alertDialog.setTitle("Delete product")
                            alertDialog.setMessage("Are you sure?")
                            alertDialog.setCancelable(false)
                            alertDialog.setIcon(R.drawable.ic_delete)

                            alertDialog.setPositiveButton("Yes") { dialogInterface, i ->
                                deleteProduct(pid!!)

                            }
                            alertDialog.setNegativeButton("No") { dialogInterface, i ->
//                                activity!!.supportFragmentManager.beginTransaction()
//                                    .replace(R.id.mainContainerAdmin, DetailsProduct()).commit()
                            }
                            alertDialog.create().show()
                        }

                    }
                    true
                }
                popup.show()
            }
        }

        root.back_to_products.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        return root
    }


    private fun deleteProduct(id:String){
        db.collection("products").document(id)
            .delete()
            .addOnSuccessListener {
                Snackbar.make(product_details_layout,"Deletion successful", Snackbar.LENGTH_LONG).show()
                fragmentManager?.popBackStack()
            }
            .addOnFailureListener {
                Snackbar.make(product_details_layout,"Deletion failed", Snackbar.LENGTH_LONG).show()
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

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0!!
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.isMyLocationEnabled = true

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style))
        val markeruser = LatLng(lat, lng)
        mMap.addMarker(MarkerOptions().position(markeruser).title("Marker in Gaza"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markeruser, 12f))

    }
}
