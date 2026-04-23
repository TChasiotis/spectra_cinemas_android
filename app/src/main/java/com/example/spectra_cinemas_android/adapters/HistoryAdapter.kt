package com.example.spectra_cinemas_android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spectra_cinemas_android.databinding.CinemaItemBinding // Χρησιμοποιούμε ένα υπάρχον binding ή φτιάχνουμε νέο
import com.example.spectra_cinemas_android.models.Ticket

class HistoryAdapter(
    private val tickets: List<Ticket>,
    private val onTicketClick: (Ticket) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: CinemaItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CinemaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.binding.cinemaName.text = ticket.movieTitle
        holder.binding.cinemaCity.text = "${ticket.date} | ${ticket.time}"
        holder.binding.cinemaAddress.text = "Θέσεις: ${ticket.seats}"
        
        holder.itemView.setOnClickListener { onTicketClick(ticket) }
    }

    override fun getItemCount() = tickets.size
}
