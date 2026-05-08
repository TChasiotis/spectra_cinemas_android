package com.example.spectra_cinemas_android.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "local_tickets")
data class Ticket(
    @PrimaryKey val orderId: String,
    val movieTitle: String,
    val cinemaName: String,
    val hallName: String,
    val date: String,
    val time: String,
    val seats: String,
    val price: String,
    val snacks: String,
    val paymentStatus: String
) : Serializable {

    // Το toString() για τη λίστα του ιστορικού
    override fun toString(): String {
        return "$movieTitle - $date $time (#$orderId)"
    }
}
