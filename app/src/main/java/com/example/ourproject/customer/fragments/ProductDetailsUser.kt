
package com.example.ourproject.customer.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.appcompat.app.AlertDialog

import com.example.ourproject.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main_nav.*
import kotlinx.android.synthetic.main.fragment_product_details_user.*
import kotlinx.android.synthetic.main.fragment_product_details_user.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProductDetailsUser : Fragment() , OnMapReadyCallback {

    var  pid= ""
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    var rate=0.toDouble()
    var userid=""
    lateinit var rateArray:ArrayList<String>
    lateinit var userRate:ArrayList<String>
    var isRate=false
    var pname=""
    var pimage=""
    var prate=0.toDouble()
    var pprice=0.toDouble()
    var pdescription=""
    var pcountPurchase=0
    var pisFavorite=false
    var plong=0.toDouble()
    var plat=0.toDouble()
    var pcategoryName=""
    var c=0f
    lateinit var myPurchase:ArrayList<String>
    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_product_details_user, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.Addproduct_location) as SupportMapFragment
        mapFragment.getMapAsync(this)


        db= Firebase.firestore
        auth = Firebase.auth
        userid=auth.currentUser!!.uid
        userRate=ArrayList<String>()
        myPurchase=ArrayList<String>()

        val b = arguments
        if (b != null) {
            pid = b.getString("pid")!!
            pisFavorite = b.getBoolean("pisFavorite")
            prate = b.getDouble("prate")
            plong=b.getDouble("plong",0.0)
            plat=b.getDouble("plat",0.0)
            if (pisFavorite == false) {
                root.favorite.setImageResource(R.drawable.ic_favorite_border)
            } else {
                root.favorite.setImageResource(R.drawable.ic_favorite_black_24dp)
            }

            root.favorite.setOnClickListener {
                if (pisFavorite == false) {
                    updateFavorite(pid, true)
                    pisFavorite = true
                    root.favorite.setImageResource(R.drawable.ic_favorite_black_24dp)
                } else {
                    updateFavorite(pid, false)
                    pisFavorite = false
                    root.favorite.setImageResource(R.drawable.ic_favorite_border)
                }
            }

            getDetails(pid)




            root.buy.setOnClickListener {
                val alertDialog = AlertDialog.Builder(activity!!)
                alertDialog.setTitle("Buy")
                alertDialog.setMessage("Are you sure?")
                alertDialog.setCancelable(false)
                alertDialog.setIcon(R.drawable.ic_shopping_cart_black)

                alertDialog.setPositiveButton("Yes") { dialogInterface, i ->
                    Snackbar.make(product_details_layout, "Buying succeeded", Snackbar.LENGTH_LONG)
                        .show()
                    updateCountPurchase(pid, pcountPurchase)
                    pcountPurchase += 1
                   // myPurchase.add(pid)

                    buy(pid)
                    myPurchases(myPurchase)
                    activity!!.nav_view.visibility = View.GONE
                }
                alertDialog.setNegativeButton("No") { dialogInterface,i ->
                    dialogInterface.dismiss()
                }
                alertDialog.create().show()
            }

            rate(userid)
            root.btnRate.setOnClickListener {
            showDialogRating()

        }
        }

        root.back_to_products.setOnClickListener {
            fragmentManager?.popBackStack()
            activity!!.nav_view.visibility=View.GONE
        }

        return root
    }

    private fun showDialogRating() {
       val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Rating product")
        builder.setMessage("Rate me")

        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_rating_comment,null)


        builder.setView(itemView)
        builder.setNegativeButton("cancel"){dialogInterface,i -> dialogInterface.dismiss()  }

        builder.setPositiveButton("ok"){dialogInterface,i ->
           var ratingBar= itemView.findViewById<RatingBar>(R.id.rating_bar)
           var ratingr=ratingBar.rating
            if (isRate==false){
                if (ratingr>=1){
                    var ratingr=ratingBar.rating.toString()
                    userRate.add(userid)
                    updateuserArray(userRate)
                    rateArray.add(ratingr)
                    updaterateArray(rateArray)
                    isRate=true
                    activity!!.supportFragmentManager.beginTransaction().replace(R.id.mainContainer,HomeFragment()).commit()
                }
            }

            }
        val dialog=builder.create()
        dialog.show()
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

    private fun updateCountPurchase(id:String, countPurchase:Int){
        val product=HashMap<String,Any>()
        product["countPurchase"]=countPurchase+1
        db.collection("products").document(id)
            .update(product)
            .addOnSuccessListener { querySnapshot ->
                Log.e("hala","updated countPurchase")
            }.addOnFailureListener { exception ->
                Log.e("hala","failed countPurchase")
            }
    }

    fun getDetails(pid:String){
        db.collection("products").document(pid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot!=null){
                    var data=documentSnapshot.data
                    pname = data!!["name"] as String
                    pimage = data["image"] as String
                    pprice = data["price"] as Double
                    prate = data["rate"] as Double
                    plat = data["locationLat"].toString().toDouble()
                    plong = data["locationLng"].toString().toDouble()
                    pisFavorite = data["isFavorite"] as Boolean
                    pcountPurchase = data["countPurchase"].toString().toInt()
                    pdescription = data["description"] as String
                    pcategoryName = data["categoryName"] as String
                    userRate = data["userRate"] as ArrayList<String>
                    rateArray = data["rateArray"] as ArrayList<String>

                    if (pimage.isNotEmpty()){
                        Picasso.get().load(pimage).into(product_image)
                    }else{
                        product_image.setImageResource(R.drawable.ic_camera)
                    }
                    product_name.text = pname
                    product_price.text = pprice.toString()
                    product_desc.text = pdescription
                    rat_bar.rating=prate.toFloat()


                }
            }
    }

    fun rate(userid:String){
        var fb=FirebaseFirestore.getInstance()
        var ref =fb.collection("products").document(pid)
        ref.get()
            .addOnSuccessListener {documentSnapshot ->
                if (documentSnapshot!=null){
                    var data=documentSnapshot.data
                    userRate=data!!["userRate"] as ArrayList<String>
                    rateArray=data["rateArray"] as ArrayList<String>
                    for (user in userRate) {
                        if (user == userid) {
                            isRate = true
                        }
                    }
                    var count=0f

                    for (rate in rateArray){
                        count+=rate.toFloat()
                        Log.e("get rate",rate)
                    }
                    c=count / rateArray.size
                    updateRate(c)

                    }

                }
            }

    fun updateRate(rate:Float){
        var fb=FirebaseFirestore.getInstance()
        var ref =fb.collection("products").document(pid)
        ref.update("rate",rate)
            .addOnSuccessListener {
                Log.e("rateeee",rate.toString())
            }
            .addOnFailureListener {

            }
    }

    private fun updateuserArray(userRate:ArrayList<String>){
        var fb=FirebaseFirestore.getInstance()
        var ref =fb.collection("products").document(pid)
        ref .update("userRate",userRate)
            .addOnSuccessListener {
                Log.e("update user array", "true")
            }
            .addOnFailureListener {
                Log.e("update user array", "false")
            }
    }

    private fun updaterateArray(rateArray:ArrayList<String>){
        var fb=FirebaseFirestore.getInstance()
        var ref =fb.collection("products").document(pid)
        ref .update("rateArray",rateArray)
            .addOnSuccessListener {
                Log.e("update rate array", "true")
            }
            .addOnFailureListener {
                Log.e("update rate array", "false")
            }
    }

    fun buy(pid:String){
        myPurchase=ArrayList<String>()
        var index=0
        var fb=FirebaseFirestore.getInstance()
        var ref =fb.collection("users").document(auth.currentUser!!.uid)
        myPurchase.add(index,pid)
        index+=1
        Log.e("purch",myPurchase.toString())
        ref.update("myPurchase",myPurchase)
            .addOnSuccessListener {documentSnapshot ->

            }
    }
    private fun myPurchases(myPurchase:ArrayList<String>){
        db!!.collection("users").whereEqualTo("id", auth.currentUser!!.uid).get()
            .addOnSuccessListener { querySnapshot ->
                db!!.collection("users").document(querySnapshot.documents.get(0).id)
                    .update("myPurchase",myPurchase)
                Log.e("update user purchase", "true")
            }
            .addOnFailureListener {
                Log.e("update user purchase", "false")
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled=true
        mMap.uiSettings.isCompassEnabled=true
        mMap.uiSettings.isMyLocationButtonEnabled=true
        mMap.uiSettings.isRotateGesturesEnabled=true
        mMap.uiSettings.isTiltGesturesEnabled=true

        val sh = activity!!.getSharedPreferences("MYPref", Context.MODE_PRIVATE)

        var lat = sh.getString("latUser", "")!!
        var lag = sh.getString("lagUser", "")!!

        // Add a marker in User and move the camera
        val markeruser = LatLng(lat.toDouble(), lag.toDouble())
        mMap.addMarker(MarkerOptions().position(markeruser).title("Marker in Gaza"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markeruser, 12f))



        var fb = FirebaseFirestore.getInstance()
        var ref = fb.collection("products").whereEqualTo("id",pid)
           // document(pid)

        ref.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null) {

                    val markerProd = LatLng(plat,plong)
                    mMap.addMarker(MarkerOptions().position(markerProd))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerProd, 12f))

                    mMap.addPolyline(
                        PolylineOptions()
                            .add(markeruser)
                            .add(markerProd)
                            .color(Color.GRAY)
                            .visible(true)
                    )
                }
                mMap = googleMap
            }
    }

}