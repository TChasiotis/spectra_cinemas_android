package com.example.spectra_cinemas_android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.databinding.CinemaItemBinding
import com.example.spectra_cinemas_android.models.Ticket
import com.example.spectra_cinemas_android.utils.CinemaData
import com.example.spectra_cinemas_android.utils.DateUtils
import com.squareup.picasso.Picasso

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
        
        // Αναζήτηση του κινηματογράφου για σωστά στοιχεία
        val cinema = CinemaData.getAllCinemas().find { 
            it.name.trim().equals(ticket.cinemaName.trim(), ignoreCase = true) 
        }

        val displayDate = DateUtils.getDisplayDate(ticket.date)
        holder.binding.cinemaName.text = ticket.movieTitle
        holder.binding.cinemaCity.text = ticket.cinemaName
        holder.binding.cinemaAddress.text = "$displayDate | ${ticket.time}"
        holder.binding.cinemaPhone.text = "Θέσεις: ${ticket.seats}"
        
        if (cinema != null) {
            if (!cinema.imageUrl.isNullOrEmpty()) {
                Picasso.get().load(cinema.imageUrl).placeholder(R.drawable.l_spectra_logo).into(holder.binding.cinemaImage)
            } else {
                holder.binding.cinemaImage.setImageResource(cinema.imageResId)
            }
            // Βάζουμε το τηλέφωνο στην πόλη/τίτλο του σινεμά για να αφήσουμε τις θέσεις μόνες τους στην τελευταία σειρά
            holder.binding.cinemaCity.text = "${ticket.cinemaName} | 📞 ${cinema.phone}"
        } else {
            holder.binding.cinemaImage.setImageResource(R.drawable.p_card_default)
        }
        
        holder.itemView.setOnClickListener { onTicketClick(ticket) }
    }

    override fun getItemCount() = tickets.size
}
