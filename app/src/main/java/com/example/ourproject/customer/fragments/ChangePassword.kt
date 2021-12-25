package com.example.ourproject.customer.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.example.ourproject.R
import com.example.ourproject.customer.LogIn
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main_nav.*
import kotlinx.android.synthetic.main.fragment_add_product.*
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.fragment_change_password.view.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*

/**
 * A simple [Fragment] subclass.
 */
class ChangePassword : Fragment() {
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_change_password, container, false)
        db= Firebase.firestore
        auth= Firebase.auth
        root.pass_cancel.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, Profile()).commit()
            activity!!.nav_view.visibility=View.VISIBLE
        }
        root.save_password.setOnClickListener {

            changePassword()

//            activity!!.supportFragmentManager.beginTransaction()
//                .replace(R.id.mainContainer, Profile()).commit()
//            activity!!.nav_view.visibility=View.VISIBLE
        }

        return root
    }

    private fun changePassword() {

        if (current_pass.text.isNotEmpty() &&
            new_pass.text.isNotEmpty() &&
            confirm_new_pass.text.isNotEmpty()
        ) {

            if (new_pass.text.toString().equals(confirm_new_pass.text.toString())) {

                val user = auth.currentUser
                if (user != null && user.email != null) {
                    val credential = EmailAuthProvider
                        .getCredential(user.email!!, current_pass.text.toString())

// Prompt the user to re-provide their sign-in credentials
                    user?.reauthenticate(credential)
                        ?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                //Toast.makeText(context, "Re-Authentication success.", Toast.LENGTH_SHORT).show()
                                user?.updatePassword(new_pass.text.toString())
                                    ?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(context, "Password changed successfully, sign in again", Toast.LENGTH_SHORT).show()
                                            auth.signOut()
                                            startActivity(Intent(context, LogIn::class.java))
                                        }
                                    }

                            } else {
                                Toast.makeText(context, "Re-Authentication failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    startActivity(Intent(context, LogIn::class.java))

                }

            } else {
                Toast.makeText(context, "Password mismatching.", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(context, "Please enter all the fields.", Toast.LENGTH_SHORT).show()
        }

    }
}