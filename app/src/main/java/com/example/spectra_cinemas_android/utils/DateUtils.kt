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

    fun isAdult(day: Int, month: Int, year: Int): Boolean {
        val today = getCurrentDate()
        val birthDate = Calendar.getInstance()
        birthDate.set(year, month - 1, day)

        var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age >= 18
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
