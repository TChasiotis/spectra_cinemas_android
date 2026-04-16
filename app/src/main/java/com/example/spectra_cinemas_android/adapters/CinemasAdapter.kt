package com.example.spectra_cinemas_android.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.models.Cinema

class CinemasAdapter(
    private val cinemasList: List<Cinema>
) : RecyclerView.Adapter<CinemasAdapter.CinemaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CinemaViewHolder {
        // "Φουσκώνει" το cinema_item.xml
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cinema_item, parent, false)
        return CinemaViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cinemasList.size
    }

    override fun onBindViewHolder(holder: CinemaViewHolder, position: Int) {
        val cinema = cinemasList[position]

        // Εδώ βάζουμε τα emojis και τα κείμενα ακριβώς όπως τα είχες στη JavaFX
        holder.nameText.text = cinema.name
        holder.cityText.text = "Πόλη: ${cinema.city}"
        holder.addressText.text = "📍 ${cinema.address}"
        holder.phoneText.text = "📞 ${cinema.phone}"

        // Η εικόνα από το drawable
        holder.imageView.setImageResource(cinema.imageResId)
    }

    // Συνδέει τα IDs του XML σου με τον κώδικα
    class CinemaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.cinemaImage)
        val nameText: TextView = itemView.findViewById(R.id.cinemaName)
        val cityText: TextView = itemView.findViewById(R.id.cinemaCity)
        val addressText: TextView = itemView.findViewById(R.id.cinemaAddress)
        val phoneText: TextView = itemView.findViewById(R.id.cinemaPhone)
    }
}