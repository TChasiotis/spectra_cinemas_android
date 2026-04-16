package com.example.spectra_cinemas_android.models

data class Movie(
    val title: String,
    val englishTitle: String,
    val description: String,
    val imageResId: Int,
    val trailerResId: Int,
    val tags: String,
    val showtimes: List<String>? = null, // Το κάναμε προαιρετικό (?)
    val releaseDate: String? = null      // Προσθέσαμε την ημερομηνία (προαιρετική)
)