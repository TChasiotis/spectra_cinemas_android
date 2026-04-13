package com.example.spectra_cinemas_android.utils

import com.example.spectra_cinemas_android.models.Ticket

object HistoryManager {
    // Μια mutable λίστα που κρατάει τα εισιτήρια στη μνήμη
    private val history = mutableListOf<Ticket>()

    fun addTicket(ticket: Ticket) {
        history.add(ticket)
    }

    fun getHistory(): List<Ticket> {
        return history
    }
}