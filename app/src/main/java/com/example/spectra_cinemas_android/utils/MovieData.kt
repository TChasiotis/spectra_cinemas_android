package com.example.spectra_cinemas_android.utils

import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.models.Movie

object MovieData {

    fun getMovies(): List<Movie> {
        return listOf(
            Movie(
                "AVATAR: ΦΩΤΙΑ ΚΑΙ ΣΤΑΧΤΗ",
                "AVATAR: FIRE AND ASH",
                "Η οικογένεια του Τζέικ και της Νεϊτίρι παλεύει με το πένθος μετά τον θάνατο του Νετεγιάμ, ενώ έρχεται αντιμέτωπη με μια νέα, επιθετική φυλή των Νά'βι...",
                R.drawable.m_avatar_poster,
                R.raw.m_avatar_trailer,
                "K13 | ACTION SCI-FI | 197'",
                listOf("18:00", "21:30")
            ),
            Movie(
                "KEEPER",
                "KEEPER",
                "Ένα ρομαντικό ταξίδι επετείου σε μια απομονωμένη καλύβα παίρνει μια δυσοίωνη τροπή όταν μια σκοτεινή παρουσία αποκαλύπτεται...",
                R.drawable.m_keeper_poster,
                R.raw.m_keeper_trailer,
                "K18 | THRILLER | 99'",
                listOf("17:00", "20:30")
            ),
            Movie(
                "ΑΓΙΑ ΝΥΧΤΑ, ΑΓΡΙΑ ΝΥΧΤΑ",
                "SILENT NIGHT, DEADLY NIGHT",
                "Ένα παιδί γίνεται μάρτυρας της δολοφονίας των γονιών του από έναν άντρα με στολή Άγιου Βασίλη...",
                R.drawable.m_silent_night_poster,
                R.raw.m_silent_night_trailer,
                "K18 | HORROR | 95'",
                listOf("16:00", "18:30", "21:00")
            ),
            Movie(
                "ΑΜΑΡΤΩΛΟΙ",
                "SINNERS",
                "Προσπαθώντας να αφήσουν πίσω τις ταραγμένες ζωές τους, δύο δίδυμα αδέρφια επιστρέφουν στη γενέτειρά τους...",
                R.drawable.m_sinners_poster,
                R.raw.m_sinners_trailer,
                "K18 | THRILLER | 138'",
                listOf("17:30", "21:15")
            ),
            Movie(
                "FIVE NIGHTS AT FREDDY'S 2",
                "FIVE NIGHTS AT FREDDY'S 2",
                "Ένα χρόνο μετά τον υπερφυσικό εφιάλτη στην πιτσαρία Freddy Fazbear’s, η Άμπι το σκάει...",
                R.drawable.m_five_nights_poster,
                R.raw.m_five_nights_trailer,
                "K18 | HORROR | 104'",
                listOf("19:00", "22:00", "00:30")
            ),
            Movie(
                "ΤΑ ΚΑΛΑΝΤΑ ΤΩΝ ΧΡΙΣΤΟΥΓΕΝΝΩΝ",
                "A CHRISTMAS CAROL",
                "Ένας σκληρόκαρδος τσιγκούνης, ονόματι Λυκούργος, έρχεται αντιμέτωπος με τρία απρόσμενα πνεύματα...",
                R.drawable.m_kalanta_poster,
                R.raw.m_kalanta_trailer,
                "K | CHRISTMAS STORY | 96'",
                listOf("19:00", "22:00", "00:30")
            ),
            Movie(
                "ΖΩΟΥΠΟΛΗ 2 (ΜΕΤΑΓΛΩΤΤΙΣΜΕΝΟ)",
                "ZOOTOPIA 2 (GREEK DUBBED)",
                "Η γενναία αστυνομικός Τζούντι Χοπς και ο φίλος της, Νικ Γουάιλντ, ενώνουν ξανά τις δυνάμεις τους...",
                R.drawable.m_zootopia_gr_poster,
                R.raw.m_zootopia_gr_trailer,
                "K | ANIMATION | 107'",
                listOf("20:00", "22:45")
            ),
            Movie(
                "ΖΩΟΥΠΟΛΗ 2 (ΥΠΟΤΙΤΛΙΣΜΕΝΟ)",
                "ZOOTOPIA 2 (GREEK SUBTITLED)",
                "Η γενναία αστυνομικός Τζούντι Χοπς και ο φίλος της, Νικ Γουάιλντ, ενώνουν ξανά τις δυνάμεις τους...",
                R.drawable.m_zootopia_en_poster,
                R.raw.m_zootopia_en_trailer,
                "K | ANIMATION | 107'",
                listOf("20:00", "22:45")
            ),
            Movie(
                "ΣΠΑΣΜΕΝΗ ΦΛΕΒΑ",
                "SPASMENI FLEVA",
                "Προκειμένου να κρατήσει το σπίτι του μακριά από τα νύχια ενός τοκογλύφου, ένας επιχειρηματίας...",
                R.drawable.m_fleva_poster,
                R.raw.m_fleva_trailer,
                "K18 | DRAMA | 127'",
                listOf("20:00", "22:45")
            ),
            Movie(
                "Η ΣΥΜΜΟΡΙΑ ΤΩΝ ΜΑΓΩΝ 3",
                "NOW YOU SEE ME: NOW YOU DON'T",
                "Μια ληστεία διαμαντιών ενώνει ξανά τους αποσυρμένους ταχυδακτυλουργούς «Horsemen»...",
                R.drawable.m_now_you_see_me_poster,
                R.raw.m_now_you_see_me_trailer,
                "K12 | MYSTERY | 112'",
                listOf("18:15", "21:45")
            ),
            Movie(
                "ΝΥΡΕΜΒΕΡΓΗ",
                "NUREMBERG",
                "Ένας ψυχίατρος του Β' Παγκοσμίου Πολέμου αξιολογεί ηγέτες των Ναζί πριν από τις δίκες της Νυρεμβέργης...",
                R.drawable.m_nuremberg_poster,
                R.raw.m_nuremberg_trailer,
                "K15 | DRAMA | 148'",
                listOf("12:00", "15:00", "17:30")
            )
        )
    }
}