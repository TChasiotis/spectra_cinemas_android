package com.example.spectra_cinemas_android.utils

import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.models.Hall

object HallData {
    fun getHalls(): List<Hall> {
        return listOf(
            Hall(
                "Spectra IMAX",
                "Spectra Cinemas Συγγρού (Αθήνα)", // Διόρθωση ονόματος για να ταιριάζει με το Cinema
                R.drawable.h_imax_prooptikh,
                "230 Θέσεις",
                "10 Σειρές",
                "4 Έξοδοι",
                "Dolby Atmos, 8K Laser Projection, Γιγαντοοθόνη 25m"
            ),
            Hall(
                "Spectra Standard",
                "Spectra Cinemas Μαρούσι (Αθήνα)", // Διόρθωση ονόματος
                R.drawable.h_standard_prooptikh,
                "200 Θέσεις",
                "10 Σειρές",
                "2 Έξοδοι",
                "Dolby 7.1, 4K Projection, Μεγάλη Οθόνη 18m"
            )
        )
    }
}
