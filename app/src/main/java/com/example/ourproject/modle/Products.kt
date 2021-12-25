package com.example.ourproject.modle

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

data class Products (var id:String?, var image:String?, var name:String?, var price:Double, var rate:Double, var location:GeoPoint, var isFavorite:Boolean
,var countPurchase:Int, var description:String?, var categoryName:String?)