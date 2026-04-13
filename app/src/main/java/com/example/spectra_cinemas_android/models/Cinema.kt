package com.example.spectra_cinemas_android.models

data class Cinema(
    val city: String,
    val name: String,
    val address: String,
    val phone: String,
    val imageResId: Int // Αλλαγή σε Int για τα drawables του Android!
) {
    // Το κρατάμε ακριβώς όπως το είχες στη Java για τα Dropdowns (Spinners)
    override fun toString(): String {
        return name
    }
}