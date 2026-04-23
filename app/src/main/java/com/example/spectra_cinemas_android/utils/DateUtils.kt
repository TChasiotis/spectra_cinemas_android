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
}
