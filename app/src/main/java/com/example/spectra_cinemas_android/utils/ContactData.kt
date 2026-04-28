package com.example.spectra_cinemas_android.utils

import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.models.Office

object ContactData {
    fun getOffices(): List<Office> {
        return listOf(
            Office(
                "Γραφεία Αθηνών",
                "Κεντρική Διοίκηση & Εξυπηρέτηση",
                R.drawable.e_athens_grafeio,
                "Λεωφόρος Κηφισίας 120, Αμπελόκηποι",
                "210 1234567",
                "athens@spectra.gr"
            ),
            Office(
                "Γραφεία Θεσσαλονίκης",
                "Διεύθυνση Βορείου Ελλάδος",
                R.drawable.e_thessaloniki_grafeio,
                "Τσιμισκή 45, Κέντρο",
                "2310 765432",
                "thessaloniki@spectra.gr"
            )
        )
    }
}
