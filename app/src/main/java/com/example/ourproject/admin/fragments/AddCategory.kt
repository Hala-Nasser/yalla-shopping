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
import androidx.core.net.toUri

import com.example.ourproject.R
import com.example.ourproject.customer.MainActivity_nav
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import kotlinx.android.synthetic.main.activity_sing_up.*
import kotlinx.android.synthetic.main.fragment_add_category.*
import kotlinx.android.synthetic.main.fragment_add_category.view.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import java.io.ByteArrayOutputStream
import kotlin.concurrent.thread
import androidx.core.view.marginTop as marginTop1

/**
 * A simple [Fragment] subclass.
 */
class AddCategory : Fragment() {

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
        val root = inflater.inflate(R.layout.fragment_add_category, container, false)

        db=Firebase.firestore
        auth=Firebase.auth
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("images")

            root.category_image.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_PICK
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
                Thread.sleep(2000)
                category_image.getLayoutParams().height = 800
                category_image.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT
                val param = category_image.layoutParams as ViewGroup.MarginLayoutParams
                param.setMargins(0,-70,0,10)
                category_image.layoutParams = param

            }

                root.save_new_category.setOnClickListener {
                    showDialog()
                    // Get the data from an ImageView as bytes
                    val bitmap = (category_image.drawable as BitmapDrawable).bitmap
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                    val data = baos.toByteArray()

                    val childRef = imageRef.child(System.currentTimeMillis().toString() + "_categoryimages.png")
                    var uploadTask = childRef.putBytes(data)
                    uploadTask.addOnFailureListener { exception ->
                        Log.e(TAG, exception.message)
                        hideDialog()
                        // Handle unsuccessful uploads
                    }.addOnSuccessListener {
                        // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                        // ...

                        Log.e(TAG, "Image Uploaded Successfully")
                        Toast.makeText(activity, "Image Uploaded Successfully", Toast.LENGTH_SHORT)
                            .show()

                        childRef.downloadUrl.addOnSuccessListener { uri ->
                            Log.e(TAG, uri.toString())
                            fileURI = uri

                            var name=root.category_name.text.toString()

                            addCategory(name,fileURI.toString())
                        }
                        hideDialog()
                    }
             }
        root.category_cancel.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainerAdmin, CategoryFragmentAdmin()).addToBackStack(null)
                .commit()
        }
        return root
    }

    private fun showDialog() {
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMessage("Adding category ...")
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
            category_image.setImageURI(imageURI)
        }
    }

     private fun addCategory(name:String,image:String){
        val category= hashMapOf("category_name" to name,"category_image" to image)
        db.collection("categories")
            .add(category)
            .addOnSuccessListener {documentReference ->
                Log.e("hala","Category added successfully with category id ${documentReference.id}")
                activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.mainContainerAdmin, CategoryFragmentAdmin()).addToBackStack(null)
                    .commit()
            }
            .addOnFailureListener {exception ->
                Log.e("hala", exception.message)
            }
    }

}
