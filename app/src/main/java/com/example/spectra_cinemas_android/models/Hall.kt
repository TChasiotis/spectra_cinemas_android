package com.example.spectra_cinemas_android.models

data class Hall(
    val title: String,
    val imageResId: Int, // Για το R.drawable...
    val seats: String,
    val rows: String,
    val exits: String,
    val tech: String
)