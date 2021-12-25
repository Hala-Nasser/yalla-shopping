//package com.example.ourproject.customer.fragments
//
//import android.app.Activity
//import android.app.Application
//import android.app.ProgressDialog
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.drawable.BitmapDrawable
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.fragment.app.FragmentActivity
//import androidx.fragment.app.FragmentManager
//import com.example.ourproject.Choice
//
//import com.example.ourproject.R
//import com.example.ourproject.customer.SingUp
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import com.google.firebase.storage.FirebaseStorage
//import com.google.firebase.storage.StorageReference
//import com.google.firebase.storage.ktx.storage
//import com.squareup.picasso.Picasso
//import com.vansuita.pickimage.bean.PickResult
//import com.vansuita.pickimage.bundle.PickSetup
//import com.vansuita.pickimage.dialog.PickImageDialog
//import com.vansuita.pickimage.listeners.IPickResult
//import kotlinx.android.synthetic.main.activity_main_nav.*
//import kotlinx.android.synthetic.main.fragment_profile.*
//import kotlinx.android.synthetic.main.fragment_profile.view.*
//import kotlinx.android.synthetic.main.fragment_profile.view.profile_name
//import java.io.ByteArrayOutputStream
//import java.util.*
//
//class Profile : Fragment()
//  //  , IPickResult
//{
//
//    private var progressDialog: ProgressDialog?=null
//    private var fileURI: Uri? = null
//    private val PICK_IMAGE_REQUEST = 111
//
//    var imageURI: Uri? = null
//    val TAG = "hzm"
//
//   var db: FirebaseFirestore? = null
////    var storage: FirebaseStorage? = null
////    var reference: StorageReference? = null
//  lateinit var auth: FirebaseAuth
////    lateinit var progressDialog: ProgressDialog
//   var path: String? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        val root=inflater.inflate(R.layout.fragment_profile, container, false)
//
//        val storage = Firebase.storage
//        val storageRef = storage.reference
//        val imageRef = storageRef.child("images")
//
//
//      db = Firebase.firestore
//     auth = Firebase.auth
////        storage = Firebase.storage
////        reference = storage!!.reference
////        progressDialog = ProgressDialog(context)
////        progressDialog.setMessage("watting")
////        progressDialog.setCancelable(false)
//
//
//        getProfileData()
//
//
//      root.profile_image.setOnClickListener {
//          //  PickImageDialog.build(PickSetup()).show(fragmentManager)
//          val intent = Intent()
//          intent.action = Intent.ACTION_PICK
//          intent.type = "image/*"
//          startActivityForResult(intent, PICK_IMAGE_REQUEST)
//        }
//
//
//root.setting.setOnClickListener {
//
//    showDialog()
//    // Get the data from an ImageView as bytes
//    val bitmap = (profile_image.drawable as BitmapDrawable).bitmap
//    val baos = ByteArrayOutputStream()
//    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
//    val data = baos.toByteArray()
//
//    val childRef = imageRef.child(System.currentTimeMillis().toString() + "_hzmimages.png")
//    var uploadTask = childRef.putBytes(data)
//    uploadTask.addOnFailureListener { exception ->
//        Log.e(TAG, exception.message)
//        hideDialog()
//        // Handle unsuccessful uploads
//    }.addOnSuccessListener {
//        // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
//        // ...
//        Log.e(TAG, "Image Uploaded Successfully")
//        Toast.makeText(context, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
//        childRef.downloadUrl.addOnSuccessListener { uri ->
//            Log.e(TAG, uri.toString())
//            fileURI = uri
//        }
//        hideDialog()
//        updateImage(fileURI.toString())
//        Picasso.get().load(fileURI).into(profile_image)
//
//    }
//        }
//
//        root.edit_profile.setOnClickListener {
//            activity!!.supportFragmentManager.beginTransaction()
//                .replace(R.id.mainContainer, EditProfile()).commit()
//            activity!!.nav_view.visibility=View.GONE
//        }
//        root.logout.setOnClickListener {
//            //كود الخروج
//            //getdtatAboutUser()
//            val alertDialog = AlertDialog.Builder(activity!!)
//            alertDialog.setTitle("Log out ")
//            alertDialog.setMessage("Are you sure?")
//            alertDialog.setCancelable(false)
//            alertDialog.setIcon(R.drawable.logout)
//
//            alertDialog.setPositiveButton("Yes") { dialogInterface, i ->
//                val i = Intent(activity, Choice::class.java)
//                startActivity(i)
//                activity!!.finish()
//            }
//            alertDialog.setNegativeButton("No") { dialogInterface, i ->
//                activity!!.supportFragmentManager.beginTransaction()
//                    .replace(R.id.mainContainer, Profile()).commit()
//            }
//            alertDialog.create().show()
//        }
//        root.my_purchases.setOnClickListener {
//            activity!!.supportFragmentManager.beginTransaction()
//                .replace(R.id.mainContainer, MyPurchases()).commit()
//            activity!!.nav_view.visibility=View.GONE
//        }
//        root.my_favorite.setOnClickListener {
//            activity!!.supportFragmentManager.beginTransaction()
//                .replace(R.id.mainContainer, MyFavorite()).commit()
//            activity!!.nav_view.visibility=View.GONE
//        }
//        root.password.setOnClickListener {
//            activity!!.supportFragmentManager.beginTransaction()
//                .replace(R.id.mainContainer, ChangePassword()).commit()
//            activity!!.nav_view.visibility=View.GONE
//        }
////        root.setting.setOnClickListener {
////            activity!!.supportFragmentManager.beginTransaction()
////                .replace(R.id.mainContainer, SettingFragment()).commit()
////            activity!!.nav_view.visibility=View.GONE
////
////        }
//        return root
//    }
//
//    fun getProfileData() {
//        db!!.collection("users").get()
//            .addOnSuccessListener { querySnapshot ->
//                profile_name.setText(querySnapshot.documents.get(0).get("username").toString())
//                profile_email.setText(auth.currentUser!!.email)
//            }.addOnFailureListener { exception ->
//
//            }
//    }
//
//    private fun updateImage(path: String?) {
//        db!!.collection("users").whereEqualTo("email", auth.currentUser!!.email).get()
//            .addOnSuccessListener { querySnapshot ->
//                db!!.collection("users").document(querySnapshot.documents.get(0).id)
//                    .update("image", path)
//            }.addOnFailureListener { exception ->
//
//            }
//   }
//
//
//
//    private fun showDialog() {
//        progressDialog = ProgressDialog(context)
//        progressDialog!!.setMessage("Uploading image ...")
//        progressDialog!!.setCancelable(false)
//        progressDialog!!.show()
//    }
//
//    private fun hideDialog(){
//        if(progressDialog!!.isShowing)
//            progressDialog!!.dismiss()
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
//            imageURI = data!!.data
//            Log.e(TAG, imageURI.toString())
//           profile_image.setImageURI(imageURI)
//        }
//    }
//
//}


package com.example.ourproject.customer.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import com.example.ourproject.Choice

import com.example.ourproject.R
import com.example.ourproject.admin.fragments.EditCategory
import com.example.ourproject.customer.SingUp
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
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*

class Profile : Fragment()  {


    var db: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null
    var reference: StorageReference? = null
    lateinit var auth: FirebaseAuth
    lateinit var progressDialog: ProgressDialog
    var path: String? = null
    private var fileURI: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root=inflater.inflate(R.layout.fragment_profile, container, false)

        activity!!.nav_view.visibility=View.VISIBLE

        db = Firebase.firestore
        auth = Firebase.auth
        storage = Firebase.storage
        reference = storage!!.reference
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("watting")
        progressDialog.setCancelable(false)
        getProfileData()




        root.edit_profile.setOnClickListener {
            val fragment = EditProfile()
            val b=Bundle()

            fragment.arguments=b
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, fragment).commit()
            activity!!.nav_view.visibility=View.GONE
        }
        root.logout.setOnClickListener {
            //كود الخروج
            //getdtatAboutUser()
            val alertDialog = AlertDialog.Builder(activity!!)
            alertDialog.setTitle("Log out ")
            alertDialog.setMessage("Are you sure?")
            alertDialog.setCancelable(false)
            alertDialog.setIcon(R.drawable.logout_black)

            alertDialog.setPositiveButton("Yes") { dialogInterface, i ->
                val i = Intent(activity, Choice::class.java)
                startActivity(i)
                activity!!.finish()
            }
            alertDialog.setNegativeButton("No") { dialogInterface, i ->
                activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.mainContainer, Profile()).commit()
            }
            alertDialog.create().show()
        }
        root.my_purchases.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, MyPurchases()).addToBackStack(null).commit()
            activity!!.nav_view.visibility=View.GONE
        }
        root.my_favorite.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, MyFavorite()).commit()
            activity!!.nav_view.visibility=View.GONE
        }
        root.password.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, ChangePassword()).commit()
            activity!!.nav_view.visibility=View.GONE
        }
        root.setting.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, SettingFragment()).commit()
            activity!!.nav_view.visibility=View.GONE

        }
        return root
    }

    fun getProfileData() {
        db!!.collection("users").whereEqualTo("email", auth.currentUser!!.email).get()
            .addOnSuccessListener { querySnapshot ->
                Picasso.get().load(querySnapshot.documents.get(0).get("image").toString()).into(profile_image)
                profile_name.setText(querySnapshot.documents.get(0).get("username").toString())
                profile_email.setText(auth.currentUser!!.email)
            }.addOnFailureListener { exception ->

            }
    }

}
