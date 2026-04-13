package com.example.spectra_cinemas_android.models

data class Movie(
    val title: String,
    val englishTitle: String,
    val description: String,
    val imageResId: Int,   // Για το Poster (π.χ. R.drawable.m_avatar_poster)
    val trailerResId: Int, // Για την εικόνα του Trailer
    val tags: String,
    val showtimes: List<String>
)