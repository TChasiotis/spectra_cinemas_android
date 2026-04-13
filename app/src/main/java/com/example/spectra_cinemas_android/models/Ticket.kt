package com.example.spectra_cinemas_android.models

data class Ticket(
    val orderId: String,
    val movieTitle: String,
    val cinemaName: String,
    val hallName: String,
    val date: String,
    val time: String,
    val seats: String,
    val price: String,
    val snacks: String,
    val paymentStatus: String
) {
    // Το toString() για τη λίστα του ιστορικού, γραμμένο με τον "έξυπνο" τρόπο της Kotlin
    override fun toString(): String {
        return "$date | $time - $movieTitle (Κωδικός: $orderId)"
    }
}