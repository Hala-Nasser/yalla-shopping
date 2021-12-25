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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add_product.*
import kotlinx.android.synthetic.main.fragment_add_product.view.*
import kotlinx.android.synthetic.main.fragment_edit_product_details.*
import kotlinx.android.synthetic.main.fragment_edit_product_details.Productdescription
import kotlinx.android.synthetic.main.fragment_edit_product_details.Productprice
import kotlinx.android.synthetic.main.fragment_edit_product_details.product_image
import kotlinx.android.synthetic.main.fragment_edit_product_details.view.*
import kotlinx.android.synthetic.main.fragment_edit_product_details.view.product_image
import java.io.ByteArrayOutputStream


class EditProductDetails : Fragment() {

    private var progressDialog: ProgressDialog?=null
    private var fileURI: Uri? = null
    private val PICK_IMAGE_REQUEST = 111

    var imageURI: Uri? = null
    val TAG = "hala"
    lateinit var auth: FirebaseAuth
    lateinit var db:FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_edit_product_details, container, false)

        db=Firebase.firestore
        auth=Firebase.auth
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("images")

        getSpecificProduct()
        root.product_image.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_PICK
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }


        val b = arguments
        if (b != null) {
            val catName=b.getString("pcategory")
            val id=b.getString("pid")
            getAllCategories(catName!!)

            root.btn_save_edit_product.setOnClickListener {

                showDialog()
                // Get the data from an ImageView as bytes
                val bitmap = (product_image.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val data = baos.toByteArray()

                val childRef = imageRef.child(System.currentTimeMillis().toString() + "_productimages.png")
                var uploadTask = childRef.putBytes(data)
                uploadTask.addOnFailureListener { exception ->
                    Log.e(TAG, exception.message)
                    hideDialog()
                    // Handle unsuccessful uploads
                }.addOnSuccessListener {
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    // ...

                    Log.e(TAG, "Image Uploaded Successfully")
                    childRef.downloadUrl.addOnSuccessListener { uri ->
                        Log.e(TAG, uri.toString())
                        fileURI = uri

                        var product_name = pname.text.toString()
                        var product_price=Productprice.text.toString()
                        var productLocation = GeoPoint(0.0,0.0)
                        var product_description=Productdescription.text.toString()
                        var category_name=product_category.selectedItem.toString()


                        updatepro(id!!,fileURI.toString(),product_name,product_price.toDouble(), product_description,category_name)
                        Snackbar.make(editProdict, "done", Snackbar.LENGTH_LONG).show()
                        activity!!.supportFragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.mainContainerAdmin, CategoryFragmentAdmin()).commit()
                    }
                    hideDialog()
                }
            }

        }


        root. edit_cancel.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        return root
    }

    private fun getSpecificProduct(){
        val b = arguments
        val pid=b!!.getString("pid")

        db.collection("products").document(pid!!)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null) {
                    val data = documentSnapshot.data
                    val name = data!!["name"] as String?
                    val image = data["image"] as String?
                    val price = data["price"] as Double
                    val description = data["description"] as String?

                    Picasso.get().load(image).into(product_image)
                    pname.setText(name)
                    Productprice.setText(price.toString())
                    Productdescription.setText(description)

                }

            }
    }

    private fun getAllCategories(catName:String){
        val spinnerList= mutableListOf<String>()
        db.collection("categories")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val data = document.data
                        val categoryName = data["category_name"] as String?
                            spinnerList.add(categoryName!!)
                    }
                    val arrayAdapter= ArrayAdapter<String>(activity!!,
                        android.R.layout.simple_list_item_1,spinnerList)
                    product_category.adapter=arrayAdapter
                    product_category.setSelection(arrayAdapter.getPosition(catName))

                }
            }
    }

    private fun updatepro(id:String,image: String, name: String, price: Double,
        /* location: GeoPoint,*/
        description: String, categoryName: String) {
//        val product= hashMapOf("name" to name,"image" to image,"price" to price,
//            /*"rate" to rate, "location" to location,*/
//            "description" to description, "categoryName" to categoryName)
        val product=HashMap<String,Any>()
        product["image"]=image
        product["name"]=name
        product["price"]=price
        product["description"]=description
        product["categoryName"]=categoryName
        db.collection("products").document(id)
            .update( product)
            .addOnSuccessListener { querySnapshot ->
                  Log.e("updateHala","update done")
            }.addOnFailureListener { exception ->
                  Log.e("updateHala","update failed")
        }
    }

    private fun showDialog() {
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMessage("Editing product ...")
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

}
