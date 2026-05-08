package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spectra_cinemas_android.MainActivity
import com.example.spectra_cinemas_android.adapters.HistoryAdapter
import com.example.spectra_cinemas_android.database.AppDatabase
import com.example.spectra_cinemas_android.databinding.HistoryViewBinding
import com.example.spectra_cinemas_android.models.Ticket
import com.example.spectra_cinemas_android.utils.DateUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private var _binding: HistoryViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HistoryViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.historyList.layoutManager = LinearLayoutManager(requireContext())
        
        if (Firebase.auth.currentUser != null) {
            loadBookingsFromCloud()
        } else {
            loadBookingsFromLocal()
        }
    }

    private fun loadBookingsFromCloud() {
        val uid = Firebase.auth.currentUser?.uid ?: return
        val currentTime = System.currentTimeMillis()
        val oneHourInMillis = 60 * 60 * 1000
        
        Firebase.firestore.collection("users").document(uid)
            .collection("bookings")
            .get()
            .addOnSuccessListener { documents ->
                val tickets = mutableListOf<Ticket>()
                val batch = Firebase.firestore.batch()
                var hasDeletions = false

                for (doc in documents) {
                    val date = doc.getString("date") ?: ""
                    val time = doc.getString("time") ?: ""
                    val showTimeMillis = DateUtils.parseBookingTimeToMillis(date, time)
                    
                    if (currentTime > (showTimeMillis + oneHourInMillis)) {
                        batch.delete(doc.reference)
                        hasDeletions = true
                    } else {
                        val ticket = Ticket(
                            doc.getString("orderId") ?: "",
                            doc.getString("movieTitle") ?: "",
                            doc.getString("cinemaName") ?: "",
                            doc.getString("hallName") ?: "",
                            date,
                            time,
                            doc.getString("seats") ?: "",
                            doc.getString("price") ?: "",
                            doc.getString("snacks") ?: "",
                            doc.getString("paymentStatus") ?: ""
                        )
                        tickets.add(ticket)
                    }
                }
                
                if (hasDeletions) batch.commit()
                updateAdapter(tickets)
            }
    }

    private fun loadBookingsFromLocal() {
        val db = AppDatabase.getDatabase(requireContext())
        val currentTime = System.currentTimeMillis()
        val oneHourInMillis = 60 * 60 * 1000

        viewLifecycleOwner.lifecycleScope.launch {
            val localTickets = db.appDao().getAllTickets()
            val validTickets = mutableListOf<Ticket>()

            localTickets.forEach { ticket ->
                val showTimeMillis = DateUtils.parseBookingTimeToMillis(ticket.date, ticket.time)
                if (currentTime > (showTimeMillis + oneHourInMillis)) {
                    db.appDao().deleteTicket(ticket)
                } else {
                    validTickets.add(ticket)
                }
            }
            updateAdapter(validTickets)
        }
    }

    private fun updateAdapter(tickets: List<Ticket>) {
        if (_binding == null) return
        val adapter = HistoryAdapter(tickets) { ticket ->
            val fragment = BookingFinalFragment.newInstanceFromHistory(ticket)
            (activity as? MainActivity)?.replaceFragment(fragment, "Εισιτήριο")
        }
        binding.historyList.adapter = adapter
        
        if (tickets.isEmpty()) {
            binding.txtNoHistory?.visibility = View.VISIBLE
        } else {
            binding.txtNoHistory?.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
