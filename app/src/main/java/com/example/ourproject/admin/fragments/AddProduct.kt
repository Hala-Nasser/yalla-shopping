
package com.example.ourproject.admin.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.ourproject.R
import com.example.ourproject.admin.MainActivityAdmin
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_add_product.*
import kotlinx.android.synthetic.main.fragment_add_product.view.*
import java.io.ByteArrayOutputStream

/**
 * A simple [Fragment] subclass.
 */
class AddProduct : Fragment() , OnMapReadyCallback {
    private var progressDialog: ProgressDialog?=null
    private var fileURI: Uri? = null
    private val PICK_IMAGE_REQUEST = 111

    var imageURI: Uri? = null
    val TAG = "hala"
    lateinit var auth: FirebaseAuth
    lateinit var db:FirebaseFirestore

    var markerB=true
    lateinit var mMap: GoogleMap
    // latitude location
    var lat= ""
    // long location
    var lng=""

    var rateArray=ArrayList<String>()
    var userRate=ArrayList<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = Firebase.firestore
        val root = inflater.inflate(R.layout.fragment_add_product, container, false)



        val mapFragment = childFragmentManager.findFragmentById(R.id.Addproduct_location) as SupportMapFragment
        mapFragment.getMapAsync(this)

        db=Firebase.firestore
        auth=Firebase.auth
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("images")


        root.product_cancel.setOnClickListener {
            val i=Intent(activity,MainActivityAdmin::class.java)
            startActivity(i)
        }

        root.product_image.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_PICK
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
            Thread.sleep(1000)
            product_image.getLayoutParams().height = 800
            product_image.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT
            val param = product_image.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0,-70,0,10)
            product_image.layoutParams = param
        }
        getAllCategories()

        root.btn_save_new_product.setOnClickListener {

            showDialog()
            // Get the data from an ImageView as bytes
            val bitmap = (product_image.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val data = baos.toByteArray()

            val childRef = imageRef.child(System.currentTimeMillis().toString() + "_categoryimages.png")
            var uploadTask = childRef.putBytes(data)
            uploadTask.addOnFailureListener { exception ->
                Log.e(TAG, exception.message)
                hideDialog()

                // Handle unsuccessful uploads
                mMap.setOnMapClickListener {point ->
                    lat = point.latitude.toString()
                    lng = point.longitude.toString()

                    if(markerB){
                        mMap.clear()
                        markerB=false
                    }
                    if(markerB== false){
                        mMap.addMarker(
                            MarkerOptions().position(LatLng(lat.toDouble(), lng.toDouble()))
                                .title("Marker in city"))

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat.toDouble(),lng.toDouble()),16f))

                        markerB =true
                    }
                }

            }.addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                // ...

                Log.e(TAG, "Image Uploaded Successfully")
                childRef.downloadUrl.addOnSuccessListener { uri ->
                    Log.e(TAG, uri.toString())
                    fileURI = uri

                    var product_name = productname.text.toString()
                    var product_price=Productprice.text.toString()
                    var product_description=Productdescription.text.toString()
                    var category_name=productcategory.selectedItem.toString()


                    addProduct(product_name,fileURI.toString(),product_price.toDouble(),
                        lat,lng,false,
                        0, product_description,category_name,userRate,rateArray)
                }
                hideDialog()
            }
        }
        return root
    }

    private fun addProduct(name:String, image:String, price:Double, locationLat: String,locationLng: String, isFavorite:Boolean
                           , countPurchase:Int, description:String?, categoryName:String?,userRate:ArrayList<String>?,rateArray:ArrayList<String>?){

        val product= hashMapOf("name" to name,"image" to image,"price" to price, "rate" to 0.0, "locationLat" to locationLat,"locationLng" to locationLng,
            "isFavorite" to isFavorite,  "countPurchase" to countPurchase, "description" to description, "categoryName" to categoryName,"userRate" to userRate, "rateArray" to rateArray)
        db.collection("products")
            .add(product)
            .addOnSuccessListener {documentReference ->
                Log.e("hala","Product added successfully with category id ${documentReference.id}")
                val i=Intent(activity,MainActivityAdmin::class.java)
                startActivity(i)
            }
            .addOnFailureListener {exception ->
                Log.e("hala", exception.message)
            }
    }

    private fun getAllCategories(){
        val spinnerList= mutableListOf<String>()
        db.collection("categories")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.e("hala", "${document.id} -> ${document.get("category_name")} ")
                        val data = document.data
                        val categoryName = data["category_name"] as String?
                        spinnerList.add(categoryName!!)

                    }
                    val arrayAdapter= ArrayAdapter<String>(activity!!,
                        android.R.layout.simple_list_item_1,spinnerList)
                    productcategory.adapter=arrayAdapter
                }
            }
    }

    private fun showDialog() {
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMessage("Adding product ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideDialog(){
        if(progressDialog!!.isShowing)
            progressDialog!!.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageURI = data!!.data
            Log.e(TAG, imageURI.toString())
            product_image.setImageURI(imageURI)
        }
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap =p0!!

        mMap.uiSettings.isZoomControlsEnabled =true
        mMap.uiSettings.isCompassEnabled=true
        mMap.uiSettings.isMyLocationButtonEnabled=true
        mMap.uiSettings.isRotateGesturesEnabled=true
        mMap.uiSettings.isTiltGesturesEnabled =true
        mMap.uiSettings.isMyLocationButtonEnabled=true
        mMap.uiSettings.isMyLocationButtonEnabled=true

        // Map type
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        mMap.setOnMapClickListener { point ->

            lat = point.latitude.toString()
            lng = point.longitude.toString()
            if (markerB) {
                mMap.clear()
                markerB = false
            }
            if (markerB == false) {
                mMap.addMarker(
                    MarkerOptions().position(LatLng(lat.toDouble(), lng.toDouble()))
                        .title("Marker in city")
                )
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            lat.toDouble(),
                            lng.toDouble()
                        ), 16f
                    )
                )
                markerB = true
            }
        }
    }
}