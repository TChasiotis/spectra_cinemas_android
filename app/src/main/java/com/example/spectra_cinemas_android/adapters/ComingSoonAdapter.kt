package com.example.spectra_cinemas_android.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spectra_cinemas_android.databinding.MovieItemBinding
import com.example.spectra_cinemas_android.models.Movie
import com.squareup.picasso.Picasso

class ComingSoonAdapter(
    private val movies: List<Movie>,
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.Adapter<ComingSoonAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(private val binding: MovieItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.movieTitle.text = movie.title
            
            if (!movie.imageUrl.isNullOrEmpty()) {
                Picasso.get().load(movie.imageUrl).into(binding.moviePoster)
            } else {
                binding.moviePoster.setImageResource(movie.imageResId)
            }
            
            // Εμφάνιση ημερομηνίας κυκλοφορίας
            binding.movieDate.visibility = View.VISIBLE
            binding.movieDate.text = "Αναμένεται: ${movie.releaseDate}"

            binding.root.setOnClickListener { onItemClick(movie) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size
}
