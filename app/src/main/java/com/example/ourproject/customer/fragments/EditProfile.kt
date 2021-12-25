package com.example.ourproject.customer.fragments

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

import com.example.ourproject.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import com.vansuita.pickimage.bean.PickResult
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import com.vansuita.pickimage.listeners.IPickResult
import kotlinx.android.synthetic.main.activity_main_nav.*
import kotlinx.android.synthetic.main.fragment_add_category.view.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*

import kotlinx.android.synthetic.main.fragment_edit_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class EditProfile : Fragment()
//, IPickResult
{

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
        val root= inflater.inflate(R.layout.fragment_edit_profile, container, false)

        db=Firebase.firestore
        auth=Firebase.auth
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("images")
        getProfileData()


        root.adit_cancel.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, Profile()).commit()
            activity!!.nav_view.visibility=View.VISIBLE

        }
        root.save_edit_profile.setOnClickListener {
            showDialog()
            // Get the data from an ImageView as bytes
            val bitmap = (profile_image_edit.drawable as BitmapDrawable).bitmap
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
                Toast.makeText(activity, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
                childRef.downloadUrl.addOnSuccessListener { uri ->
                    Log.e(TAG, uri.toString())
                    fileURI = uri
                    var name=root.txt_name.text.toString()
                    updateImage(fileURI.toString(),name/*,txt_email.text.toString()*/)
                    Picasso.get().load(fileURI.toString()).into(root.profile_image_edit)
                }
                hideDialog()
                activity!!.supportFragmentManager.beginTransaction().replace(R.id.mainContainer,Profile()).commit()
            }

        }
        root.edit_profile_image.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_PICK
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
        return root
    }

    private fun showDialog() {
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMessage("waitting to save ...")
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
            profile_image_edit.setImageURI(imageURI)
        }
    }
    private fun updateImage(path: String?,name:String/*,email:String*/) {
        db!!.collection("users").whereEqualTo("email", auth.currentUser!!.email).get()
            .addOnSuccessListener { querySnapshot ->
                db!!.collection("users").document(querySnapshot.documents.get(0).id)
                    .update("image", path,"username",name/*,auth.currentUser!!.email,email*/)
            }.addOnFailureListener { exception ->

            }
    }
    fun getProfileData() {
        db!!.collection("users").whereEqualTo("email", auth.currentUser!!.email).get()
            .addOnSuccessListener { querySnapshot ->
                Picasso.get().load(querySnapshot.documents.get(0).get("image").toString()).into(profile_image_edit)
                txt_name.setText(querySnapshot.documents.get(0).get("username").toString())
                txt_email.setText(auth.currentUser!!.email)
            }.addOnFailureListener { exception ->

            }
    }
}
