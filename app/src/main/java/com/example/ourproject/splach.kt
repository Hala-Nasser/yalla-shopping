package com.example.ourproject

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.animation.Animation
import android.webkit.PermissionRequest
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import com.example.ourproject.customer.MainActivity_nav
import com.example.ourproject.customer.SingUp
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.animation.AnimationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_choice.*

class splach : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    var mLastLocation =""
    var db: FirebaseFirestore? = null
    var firebase: FirebaseUser?=null

    lateinit var topAnim: Animation
    lateinit var bottomAnim:Animation
    lateinit var img: ImageView
    lateinit var app_name: TextView

    // point location client
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    val PERMISSION_ID = 250

    // latitude location
    var latitude =0.0

    // long location
    var long =0.0

    var mLocationRequest: LocationRequest? = null
    var mGoogleApiClient: GoogleApiClient? = null
    val REQUEST_LOCATION = 199
    var result: PendingResult<LocationSettingsResult>? = null
    private var locationManager: LocationManager? = null
    private var handler: Handler? = null
    private val SPLASH_DISPLAY_LENGTH = 3000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splach)

        db = Firebase.firestore

        // get location
        mFusedLocationClient= LocationServices.getFusedLocationProviderClient(this)
        //call my function get location
        getLastLocation()

        topAnim =android.view.animation.AnimationUtils.loadAnimation(this,R.anim.top_animation)
        bottomAnim =android.view.animation.AnimationUtils.loadAnimation(this,R.anim.bottom_animation)

        img=findViewById(R.id.logo)
        app_name=findViewById(R.id.app_name)

        img.animation=topAnim
        app_name.animation=bottomAnim

         handler= Handler()
            requestStoragePermission()

    }

    private fun requestStoragePermission() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    report.areAllPermissionsGranted()
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog()
                    } else {
                        if (ActivityCompat.checkSelfPermission(
                                this@splach,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                this@splach,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this@splach,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                1
                            )
                        } else {
                            mFusedLocationClient =
                                LocationServices.getFusedLocationProviderClient(this@splach)
                            mLocationRequest = LocationRequest.create()
                            mLocationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            if (isLocationServiceEnabled(this@splach)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (ContextCompat.checkSelfPermission(
                                            this@splach,
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        )
                                        == PackageManager.PERMISSION_GRANTED
                                    ) {
                                        mFusedLocationClient!!.requestLocationUpdates(
                                            mLocationRequest,
                                            mLocationCallback,
                                            Looper.myLooper()
                                        )
                                    } //Request Location Permission
                                } else {
                                    mFusedLocationClient!!.requestLocationUpdates(
                                        mLocationRequest,
                                        mLocationCallback,
                                        Looper.myLooper()
                                    )
                                }
                                if (isLocationServiceEnabled(this@splach)) {
                                    handler!!.postDelayed(Runnable {
                                        val intent=Intent(this@splach,welcome::class.java)
                                        startActivity(intent)
                                        finish()
                                    },2000)
                                }
                            } else {
                                mGoogleApiClient = GoogleApiClient.Builder(this@splach)
                                    .addConnectionCallbacks(this@splach)
                                    .addOnConnectionFailedListener(this@splach)
                                    .addApi(LocationServices.API)
                                    .build()
                                mGoogleApiClient!!.connect()
                            }
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<com.karumi.dexter.listener.PermissionRequest?>?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener { error: DexterError? ->
                Toast.makeText(
                    applicationContext,
                    "try_again",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .onSameThread()
            .check()
    }

    private fun showSettingsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@splach)
        builder.setTitle("NeedPermissions")
        builder.setMessage("PermissionsDes")
        builder.setPositiveButton("GOTOSETTINGS") { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    fun isLocationServiceEnabled(context: Context): Boolean {
        var gps_enabled = false
        var network_enabled = false
        try {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            val criteria = Criteria()
            criteria.powerRequirement =
                Criteria.POWER_MEDIUM // Chose your desired power consumption level.
            criteria.accuracy = Criteria.ACCURACY_MEDIUM // Choose your accuracy requirement.
            criteria.isSpeedRequired = true // Chose if speed for first location fix is required.
            criteria.isAltitudeRequired = true // Choose if you use altitude.
            criteria.isBearingRequired = true // Choose if you use bearing.
            criteria.isCostAllowed = true // Choose if this provider can waste money :-)
            locationManager!!.getBestProvider(criteria, true)
            gps_enabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            network_enabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: IndexOutOfBoundsException) {
            ex.message
        }
        return gps_enabled || network_enabled
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOCATION -> when (resultCode) {
                Activity.RESULT_OK -> requestStoragePermission()
                Activity.RESULT_CANCELED -> finish()
                else -> {
                }
            }
            else -> {
            }
        }
    }

    override fun onConnected(@Nullable bundle: Bundle?) {
        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(3 * 1000)
            .setFastestInterval(1000)
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest!!)
        builder.setAlwaysShow(true)
        result =
            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
        result!!.setResultCallback(
            ResultCallback { result: LocationSettingsResult ->
                val status: Status = result.status
                when (status.getStatusCode()) {
                    LocationSettingsStatusCodes.SUCCESS -> {
                    }
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        status.startResolutionForResult(this@splach, REQUEST_LOCATION)
                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        )
    }


    override fun onConnectionSuspended(i: Int) {}

    @SuppressLint("MissingPermission")
    private fun  getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        latitude = location.latitude
                        long = location.longitude

                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation

        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }



    override fun onStop() {
        super.onStop()
        val sh =getSharedPreferences("MYPref",Context.MODE_PRIVATE)
        val editor =sh.edit()
        editor.putString("lagUser",latitude.toString())
        editor.putString("latUser",long.toString())
        val e = editor.commit()
        if(e){
            //Toast.makeText(this,"Success get location",Toast.LENGTH_LONG).show()
        } else{
            Toast.makeText(this,"Failed get location",Toast.LENGTH_LONG).show()

            val i=Intent(this,welcome::class.java)
            startActivity(i)
        }
        firebase=FirebaseAuth.getInstance().currentUser
        if(firebase !=null){
            val i= Intent(this, MainActivity_nav::class.java)
            startActivity(i)
            finish()
        }else{
            val i= Intent(this, welcome::class.java)
            startActivity(i)
            finish()
        }

//        if (mGoogleApiClient != null) {
//            mGoogleApiClient!!.disconnect()
//        }
//        if (mFusedLocationClient != null) {
//            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
//        }

    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

}