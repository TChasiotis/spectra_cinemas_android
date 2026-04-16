package com.example.spectra_cinemas_android.utils

import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.models.Hall

object HallData {
    fun getHalls(): List<Hall> {
        return listOf(
            Hall(
                "Spectra IMAX",
                R.drawable.h_imax_prooptikh, // Βάζω το όνομα όπως το είδα στο XML σου
                "230 Θέσεις",
                "10 Σειρές",
                "4 Έξοδοι",
                "Dolby Atmos, 8K Laser Projection, Γιγαντοοθόνη 25m"
            ),
            Hall(
                "Spectra Standard",
                R.drawable.h_standard_prooptikh, // Υποθέτω αυτό είναι το όνομα του 2ου drawable
                "200 Θέσεις",
                "10 Σειρές",
                "2 Έξοδοι",
                "Dolby 7.1, 4K Projection, Μεγάλη Οθόνη 18m"
            )
        )
    }
}