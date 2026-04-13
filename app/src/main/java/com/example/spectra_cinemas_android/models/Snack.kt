package com.example.spectra_cinemas_android.models

data class Snack(
    val name: String,
    val price: Double,
    val type: String, // π.χ. "Food" ή "Drink"
    val imageResId: Int
)