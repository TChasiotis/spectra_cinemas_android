package com.example.spectra_cinemas_android.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.models.Movie

class MoviesAdapter(
    private val moviesList: List<Movie>,
    private val onMovieClick: (Movie) -> Unit
) : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int {
        return moviesList.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = moviesList[position]

        holder.titleText.text = movie.title
        holder.posterImage.setImageResource(movie.imageResId)

        // Η ΜΑΓΕΙΑ ΕΔΩ: Ελέγχουμε αν υπάρχει ημερομηνία
        if (movie.releaseDate != null) {
            holder.dateText.visibility = View.VISIBLE
            holder.dateText.text = "Αναμένεται: ${movie.releaseDate}"
        } else {
            holder.dateText.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onMovieClick(movie)
        }
    }

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val posterImage: ImageView = itemView.findViewById(R.id.moviePoster)
        val titleText: TextView = itemView.findViewById(R.id.movieTitle)

        // Προσθέσαμε το TextView της ημερομηνίας
        val dateText: TextView = itemView.findViewById(R.id.movieDate)
    }
}