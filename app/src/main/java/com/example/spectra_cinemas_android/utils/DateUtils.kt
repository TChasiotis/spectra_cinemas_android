package com.example.spectra_cinemas_android.utils

import java.util.*

object DateUtils {

    fun getCurrentDate(): Calendar {
        return Calendar.getInstance()
    }

    fun getYearsList(): List<String> {
        val currentYear = getCurrentDate().get(Calendar.YEAR)
        val years = mutableListOf<String>()
        for (i in currentYear downTo currentYear - 100) {
            years.add(i.toString())
        }
        return years
    }

    fun getMonthsList(): List<String> {
        return (1..12).map { it.toString().padStart(2, '0') }
    }

    fun getDaysList(): List<String> {
        return (1..31).map { it.toString().padStart(2, '0') }
    }

    fun getAge(day: Int, month: Int, year: Int): Int {
        val today = getCurrentDate()
        val birthDate = Calendar.getInstance()
        birthDate.set(year, month - 1, day)

        var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    fun isAdult(day: Int, month: Int, year: Int): Boolean {
        return getAge(day, month, year) >= 15
    }
    
    fun canUseCard(day: Int, month: Int, year: Int): Boolean {
        return getAge(day, month, year) >= 18
    }

    /**
     * Επιστρέφει "Σήμερα" αν η ημερομηνία αντιστοιχεί στην τρέχουσα μέρα,
     * αλλιώς επιστρέφει την ίδια την ημερομηνία.
     */
    fun getDisplayDate(dateStr: String): String {
        if (dateStr.lowercase() == "σήμερα") return "Σήμερα"
        
        val sdf = java.text.SimpleDateFormat("EEEE d/M", java.util.Locale("el", "GR"))
        val todayStr = sdf.format(Calendar.getInstance().time).replaceFirstChar { it.uppercase() }
        
        return if (dateStr.trim().equals(todayStr.trim(), ignoreCase = true)) "Σήμερα" else dateStr
    }

    /**
     * Μετατρέπει την ημερομηνία της εφαρμογής (π.χ. "Σήμερα", "Τρίτη 1/4") και την ώρα σε milliseconds
     */
    fun parseBookingTimeToMillis(dateStr: String, timeStr: String): Long {
        val calendar = Calendar.getInstance()
        try {
            val timeParts = timeStr.split(":")
            if (timeParts.size < 2) return 0
            
            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()

            if (dateStr.lowercase() != "σήμερα" && dateStr.isNotEmpty()) {
                // Αν δεν είναι σήμερα, ψάχνουμε την ημερομηνία (π.χ. "Τρίτη 1/4")
                val parts = dateStr.split(" ")
                val dayMonth = parts.last() // Παίρνουμε το "1/4"
                if (dayMonth.contains("/")) {
                    val dmParts = dayMonth.split("/")
                    calendar.set(Calendar.DAY_OF_MONTH, dmParts[0].toInt())
                    calendar.set(Calendar.MONTH, dmParts[1].toInt() - 1)
                }
            }
            
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            
            return calendar.timeInMillis
        } catch (e: Exception) {
            return 0 // Σε περίπτωση λάθους, επιστρέφουμε 0 για να διαγραφεί η κράτηση ως "πολύ παλιά"
        }
    }

    /**
     * Ελέγχει αν μια κάρτα έχει λήξει με βάση τον μήνα και το έτος (MM/YY)
     */
    fun isCardExpired(month: Int, yearYY: Int): Boolean {
        val today = getCurrentDate()
        val currentYear = today.get(Calendar.YEAR)
        val currentMonth = today.get(Calendar.MONTH) + 1 // Calendar months are 0-11

        val fullYear = 2000 + yearYY
        
        return if (fullYear < currentYear) {
            true
        } else if (fullYear == currentYear) {
            month < currentMonth
        } else {
            false
        }
    }
}
