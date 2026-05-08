package com.example.spectra_cinemas_android.utils

import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.models.Cinema

object CinemaData {

    private val cinemas = mutableListOf<Cinema>()

    init {
        // --- ΑΘΗΝΑ ---
        cinemas.add(Cinema(
            "Spectra Cinemas Συγγρού (Αθήνα)",
            "Αθήνα",
            "Λεωφόρος Συγγρού 87, 117 45",
            "210 9223344",
            R.drawable.k_athens_syggrou
        ))

        cinemas.add(Cinema(
            "Spectra Cinemas Μαρούσι (Αθήνα)",
            "Αθήνα",
            "Λεωφ. Κηφισίας 37Α, 151 23",
            "210 6811223",
            R.drawable.k_athens_marousi
        ))

        // --- ΘΕΣΣΑΛΟΝΙΚΗ ---
        cinemas.add(Cinema(
            "Spectra Cinemas Παραλία (Θεσσαλονίκη)",
            "Θεσσαλονίκη",
            "Λεωφ. Μεγάλου Αλεξάνδρου 12, 546 40",
            "2310 889900",
            R.drawable.k_thessaloniki_paralia
        ))

        cinemas.add(Cinema(
            "Spectra Cinemas Retail Park (Θεσσαλονίκη)",
            "Θεσσαλονίκη",
            "Λεωφ. Γεωργικής Σχολής 84 (Περιοχή IKEA)",
            "2310 477111",
            R.drawable.k_thessaloniki_retail_park
        ))

        // --- ΕΠΑΡΧΙΑ ---
        cinemas.add(Cinema(
            "Spectra Cinemas Πάτρα",
            "Πάτρα",
            "Ακτή Δυμαίων 17, 262 22",
            "2610 334455",
            R.drawable.k_patra
        ))

        cinemas.add(Cinema(
            "Spectra Cinemas Ηράκλειο",
            "Ηράκλειο",
            "Λεωφόρος Κνωσού 90, 713 06",
            "2810 223344",
            R.drawable.k_irakleio
        ))

        cinemas.add(Cinema(
            "Spectra Cinemas Λάρισσα",
            "Λάρισα",
            "Ηρώων Πολυτεχνείου 14, 412 21",
            "2410 551122",
            R.drawable.k_larissa
        ))

        cinemas.add(Cinema(
            "Spectra Cinemas Ιωάννινα",
            "Ιωάννινα",
            "Λεωφόρος Δωδώνης 42, 453 32",
            "2651 077889",
            R.drawable.k_ioannina
        ))
    }

    fun getAllCinemas(): List<Cinema> {
        return cinemas
    }

    fun getCinemaNames(): List<String> {
        return cinemas.map { it.name }
    }
}
