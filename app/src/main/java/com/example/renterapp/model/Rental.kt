package com.example.renterapp.model

import com.google.firebase.firestore.DocumentId


data class Rental(
    var userId: String = "",
    @DocumentId
    var id: String = "",
    var rentalType: String = "",
    var numberOfRooms: String = "",
    var monthlyPrice: Int = 0,
    var imageUrl: String = "",
    var address: String = "",
    var longitude: Double = 0.0,
    var latitude: Double = 0.0,
    @JvmField
    var isAvailable: Boolean = true
)