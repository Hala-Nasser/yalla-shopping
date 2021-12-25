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
import android.widget.Toast
import com.example.ourproject.Adapter.CategoriesAdapter

import com.example.ourproject.R
import com.example.ourproject.modle.Category
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add_category.*
import kotlinx.android.synthetic.main.fragment_edit_category.*
import kotlinx.android.synthetic.main.fragment_edit_category.view.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*

import kotlinx.android.synthetic.main.fragment_products_admin.view.*
import java.io.ByteArrayOutputStream


class EditCategory : Fragment() {
    private var progressDialog: ProgressDialog?=null
    private var fileURI: Uri? = null
    private val PICK_IMAGE_REQUEST = 111
    var imageURI: Uri? = null
    val TAG = "hala"
    lateinit var auth: FirebaseAuth
    lateinit var db:FirebaseFirestore
    var catImage:String?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_edit_category, container, false)

        db=Firebase.firestore
        auth=Firebase.auth
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("images")


        val b = arguments
        if (b != null) {
            val catId = b.getString("id")
            val catImage = b.getString("catImage")
            val catName = b.getString("catName")
            Picasso.get().load(catImage).into(root.category_image_edit)
            //root.txt_name.text = catName
            root.category_name_edit.setText(catName)
            Toast.makeText(activity!!, "$catName", Toast.LENGTH_LONG).show()
        }

        val catId=b?.getString("id")

        catImage = b?.getString("catImage")
        root.save_new_category.setOnClickListener {
            showDialog()
            // Get the data from an ImageView as bytes
            val bitmap = (category_image_edit.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val data = baos.toByteArray()

            val childRef = imageRef.child(System.currentTimeMillis().toString() + "_profileimages.png")
            var uploadTask = childRef.putBytes(data)
            uploadTask.addOnFailureListener { exception ->
                Log.e(TAG, exception.message)
                hideDialog()
                // Handle unsuccessful uploads
            }.addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                // ...
                Log.e(TAG, "Image Uploaded Successfully")
               // Toast.makeText(activity, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
                childRef.downloadUrl.addOnSuccessListener { uri ->
                    Log.e(TAG, uri.toString())
                    fileURI = uri
                    updatecat(category_name_edit.text.toString(),fileURI.toString())
                    fragmentManager?.popBackStack()
                }
                hideDialog()
            }
            Snackbar.make(edit_category_layout, "done", Snackbar.LENGTH_LONG).show()
        }

        root.category_image_edit.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_PICK
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
        root.category_cancel.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        return root
    }


    private fun showDialog() {
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMessage("Editting Category...")
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
            category_image_edit.setImageURI(imageURI)
        }
    }

    private fun updatecat(categoryName:String,path: String?) {
        db!!.collection("categories").whereEqualTo("category_image", catImage).get()
            .addOnSuccessListener { querySnapshot ->
                db!!.collection("categories").document(querySnapshot.documents.get(0).id)
                    .update("category_image", path,"category_name",categoryName)
            }.addOnFailureListener { exception ->

            }
    }
//    private fun updatecatProduct(categoryName:String,categoryNameNew:String) {
//        db!!.collection("products").whereEqualTo("categoryName", categoryName).get()
//            .addOnSuccessListener { querySnapshot ->
//                db!!.collection("products").document(querySnapshot.documents.get(0).id)
//                    .update("categoryName", categoryNameNew)
//            }.addOnFailureListener { exception ->
//
//            }
//    }
}




