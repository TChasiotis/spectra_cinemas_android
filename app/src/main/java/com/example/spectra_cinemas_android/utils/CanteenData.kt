package com.example.spectra_cinemas_android.utils

import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.models.Snack

object CanteenData {
    fun getAllSnacks(): List<Snack> {
        return listOf(
            Snack("ΠΟΠΚΟΡΝ ΜΙΚΡΟ", 4.50, "SNACK", R.drawable.f_popcorn),
            Snack("ΠΟΠΚΟΡΝ ΜΕΣΑΙΟ", 5.50, "SNACK", R.drawable.f_popcorn),
            Snack("ΠΟΠΚΟΡΝ ΜΕΓΑΛΟ", 6.50, "SNACK", R.drawable.f_popcorn),
            Snack("NACHOS DORITOS", 5.50, "SNACK", R.drawable.f_nachos_doritos_menu),
            Snack("ΣΑΛΤΣΑ ΝΤΟΜΑΤΑΣ", 0.80, "SNACK", R.drawable.f_tomatosauce),
            Snack("ΣΑΛΤΣΑ CHEDDAR", 1.20, "SNACK", R.drawable.f_cheesesauce),
            Snack("ΠΑΤΑΤΑΚΙΑ STICKS", 2.20, "SNACK", R.drawable.f_tsakiris_sticks),
            Snack("ΠΑΤΑΤΑΚΙΑ ΑΛΑΤΙ", 2.00, "SNACK", R.drawable.f_tsakiris_alati),
            Snack("ΠΑΤΑΤΑΚΙΑ ΡΙΓΑΝΗ", 2.00, "SNACK", R.drawable.f_tsakiris_rigani),
            Snack("M&MS ΣΟΚΟΛΑΤΑ", 3.50, "SNACK", R.drawable.f_mms_chocolate),
            Snack("M&MS ΦΥΣΤΙΚΙ", 3.50, "SNACK", R.drawable.f_mms_peanut),
            Snack("HARIBO", 2.50, "SNACK", R.drawable.f_haribo),
            Snack("COCA COLA", 2.80, "DRINK", R.drawable.f_coca_cola_cup),
            Snack("COCA COLA LIGHT", 2.80, "DRINK", R.drawable.f_coca_cola_light_cup),
            Snack("COCA COLA ZERO", 2.80, "DRINK", R.drawable.f_coca_cola_zero_cup),
            Snack("FANTA ΠΟΡΤΟΚΑΛΑΔΑ", 2.80, "DRINK", R.drawable.f_fanta_cup),
            Snack("FANTA ΛΕΜΟΝΑΔΑ", 2.80, "DRINK", R.drawable.f_fanta_lemonade_cup),
            Snack("SPRITE", 2.80, "DRINK", R.drawable.f_sprite_cup),
            Snack("SPRITE ZERO", 2.80, "DRINK", R.drawable.f_sprite_zero_cup),
            Snack("LIPTON ICE TEA ΡΟΔΑΚΙΝΟ", 2.80, "DRINK", R.drawable.f_lipton_peach_cup),
            Snack("LIPTON ICE TEA ΛΕΜΟΝΙ", 2.80, "DRINK", R.drawable.f_lipton_lemon_cup),
            Snack("ΝΕΡΟ 0.5L", 0.50, "DRINK", R.drawable.f_theoni)
        )
    }
}
