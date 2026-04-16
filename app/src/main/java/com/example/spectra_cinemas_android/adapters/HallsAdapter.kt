package com.example.spectra_cinemas_android.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.models.Hall

class HallsAdapter(
    private val hallsList: List<Hall>
) : RecyclerView.Adapter<HallsAdapter.HallViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HallViewHolder {
        // Φουσκώνει το δικό σου hall_item.xml
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hall_item, parent, false)
        return HallViewHolder(view)
    }

    override fun getItemCount(): Int {
        return hallsList.size
    }

    override fun onBindViewHolder(holder: HallViewHolder, position: Int) {
        val hall = hallsList[position]

        holder.titleText.text = hall.title

        // Φτιάχνουμε το string με τα emojis ακριβώς όπως το έκανες στη Java
        holder.specsText.text = "💺 ${hall.seats} | 📏 ${hall.rows} | 🚪 ${hall.exits}"

        holder.techText.text = "🛠 ${hall.tech}"
        holder.imageView.setImageResource(hall.imageResId)
    }

    class HallViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.hallImage)
        val titleText: TextView = itemView.findViewById(R.id.hallTitle)
        val specsText: TextView = itemView.findViewById(R.id.hallSpecs)
        val techText: TextView = itemView.findViewById(R.id.hallTech)
    }
}