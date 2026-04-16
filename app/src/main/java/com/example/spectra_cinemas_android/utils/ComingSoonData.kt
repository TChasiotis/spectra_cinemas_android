package com.example.spectra_cinemas_android.utils

import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.models.Movie

object ComingSoonData {

    fun getComingSoonMovies(): List<Movie> {
        return listOf(
            Movie(
                "Η ΑΓΓΕΛΙΑ",
                "THE HOUSEMAID",
                "Μια γυναίκα που αντιμετωπίζει δυσκολίες είναι χαρούμενη που κάνει μια νέα αρχή...",
                R.drawable.cs_housemaid_poster,
                R.raw.cs_housemaid_trailer,
                "R | PSYCHOLOGICAL THRILLER | 131'",
                null,
                "18 ΔΕΚΕΜΒΡΙΟΥ 2025"
            ),
            Movie(
                "ΚΑΠΟΔΙΣΤΡΙΑΣ",
                "KAPODISTRIAS",
                "Ένα ιστορικό δράμα για τον Ιωάννη Καποδίστρια, τον πρώτο Κυβερνήτη της Ελλάδας...",
                R.drawable.cs_kapodistrias_poster,
                R.raw.cs_kapodistrias_trailer,
                "K | DRAMA | 128'",
                null,
                "25 ΔΕΚΕΜΒΡΙΟΥ 2025"
            ),
            Movie(
                "ΑΝΑΚΟΝΤΑ",
                "ANACONDA",
                "Μια παρέα φίλων περνάει κρίση μέσης ηλικίας. Αποφασίζουν να γυρίσουν ξανά...",
                R.drawable.cs_anaconda_poster,
                R.raw.cs_anaconda_trailer,
                "K13 | COMEDY ACTION| 100'",
                null,
                "1 ΙΑΝΟΥΑΡΙΟΥ 2026"
            ),
            Movie(
                "ΘΑΒΟΥΜΕ ΤΟΥΣ ΝΕΚΡΟΥΣ",
                "WE BURY THE DEAD",
                "Μετά από μια καταστροφική στρατιωτική πανωλεθρία, οι νεκροί δεν ανασταίνονται απλώς...",
                R.drawable.cs_we_bury_poster,
                R.raw.cs_we_bury_trailer,
                "K18 | HORROR | 94'",
                null,
                "1 ΙΑΝΟΥΑΡΙΟΥ 2026"
            ),
            Movie(
                "GREENLAND 2",
                "GREENLAND 2",
                "Η επιζήσασα οικογένεια Γκάριτι πρέπει να εγκαταλείψει την ασφάλεια του καταφυγίου...",
                R.drawable.cs_greenland_poster,
                R.raw.cs_greenland_trailer,
                "K13 | SURVIVAL | 86'",
                null,
                "8 ΙΑΝΟΥΑΡΙΟΥ 2026"
            ),
            Movie(
                "PRIMATE",
                "PRIMATE",
                "Οι τροπικές διακοπές μιας παρέας φίλων μετατρέπονται σε μια τρομακτική...",
                R.drawable.cs_primate_poster,
                R.raw.cs_primate_trailer,
                "K18 | HORROR | 89'",
                null,
                "8 ΙΑΝΟΥΑΡΙΟΥ 2026"
            ),
            Movie(
                "28 ΧΡΟΝΙΑ ΜΕΤΑ: Ο ΝΑΟΣ ΤΩΝ ΟΣΤΩΝ",
                "28 YEARS LATER: THE BONE TEMPLE",
                "Καθώς ο Σπάικ εντάσσεται στη συμμορία του Τζίμι Κρίσταλ στην ηπειρωτική χώρα...",
                R.drawable.cs_bone_temple_poster,
                R.raw.cs_bone_temple_trailer,
                "K18 | HORROR | 109'",
                null,
                "15 ΙΑΝΟΥΑΡΙΟΥ 2026"
            )
        )
    }
}