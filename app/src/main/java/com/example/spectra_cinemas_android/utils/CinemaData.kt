package com.example.spectra_cinemas_android.utils

import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.models.Cinema

object CinemaData {

    private val cinemas = mutableListOf<Cinema>()

    init {
        // --- ΑΘΗΝΑ ---
        cinemas.add(Cinema(
            "Αθήνα",
            "Spectra Cinemas Συγγρού (Αθήνα)",
            "Λεωφόρος Συγγρού 87, 117 45",
            "210 9223344",
            R.drawable.k_athens_syggrou
        ))

        cinemas.add(Cinema(
            "Αθήνα",
            "Spectra Cinemas Μαρούσι (Αθήνα)",
            "Λεωφ. Κηφισίας 37Α, 151 23",
            "210 6811223",
            R.drawable.k_athens_marousi
        ))

        // --- ΘΕΣΣΑΛΟΝΙΚΗ ---
        cinemas.add(Cinema(
            "Θεσσαλονίκη",
            "Spectra Cinemas Παραλία (Θεσσαλονίκη)",
            "Λεωφ. Μεγάλου Αλεξάνδρου 12, 546 40",
            "2310 889900",
            R.drawable.k_thessaloniki_paralia
        ))

        cinemas.add(Cinema(
            "Θεσσαλονίκη",
            "Spectra Cinemas Retail Park (Θεσσαλονίκη)",
            "Λεωφ. Γεωργικής Σχολής 84 (Περιοχή IKEA)",
            "2310 477111",
            R.drawable.k_thessaloniki_retail_park
        ))

        // --- ΕΠΑΡΧΙΑ ---
        cinemas.add(Cinema(
            "Πάτρα",
            "Spectra Cinemas Πάτρα",
            "Ακτή Δυμαίων 17, 262 22",
            "2610 334455",
            R.drawable.k_patra
        ))

        cinemas.add(Cinema(
            "Ηράκλειο",
            "Spectra Cinemas Ηράκλειο",
            "Λεωφόρος Κνωσού 90, 713 06",
            "2810 223344",
            R.drawable.k_irakleio
        ))

        cinemas.add(Cinema(
            "Λάρισα",
            "Spectra Cinemas Λάρισσα",
            "Ηρώων Πολυτεχνείου 14, 412 21",
            "2410 551122",
            R.drawable.k_larissa
        ))

        cinemas.add(Cinema(
            "Ιωάννινα",
            "Spectra Cinemas Ιωάννινα",
            "Λεωφόρος Δωδώνης 42, 453 32",
            "2651 077889",
            R.drawable.k_ioannina
        ))
    }

    fun getAllCinemas(): List<Cinema> {
        return cinemas
    }

    // Η Kotlin μας επιτρέπει να φιλτράρουμε και να μετατρέπουμε λίστες σε μια γραμμή!
    fun getCinemaNames(): List<String> {
        return cinemas.map { it.name }
    }
}