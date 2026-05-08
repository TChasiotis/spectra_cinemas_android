package com.example.spectra_cinemas_android.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.databinding.ComingSoonDetailsViewBinding
import com.example.spectra_cinemas_android.models.Movie
import com.example.spectra_cinemas_android.utils.ComingSoonData
import com.example.spectra_cinemas_android.utils.VideoPlayer
import com.squareup.picasso.Picasso

class ComingSoonDetailsFragment : Fragment() {

    private var _binding: ComingSoonDetailsViewBinding? = null
    private val binding get() = _binding!!
    private var currentMovie: Movie? = null

    companion object {
        fun newInstance(movieTitle: String): ComingSoonDetailsFragment {
            val fragment = ComingSoonDetailsFragment()
            val args = Bundle()
            args.putString("MOVIE_TITLE", movieTitle)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ComingSoonDetailsViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movieTitle = arguments?.getString("MOVIE_TITLE")
        currentMovie = ComingSoonData.getComingSoonMovies().find { it.title == movieTitle }

        currentMovie?.let { setupPage(it) }
    }

    private fun setupPage(movie: Movie) {
        binding.titleLabel.text = movie.title
        binding.subtitleLabel.text = movie.englishTitle
        binding.descriptionText.text = movie.description
        binding.releaseDateLabel.text = "Αναμένεται: ${movie.releaseDate}"

        val tags = movie.tags.split("|")
        if (tags.isNotEmpty()) binding.ratingLabel.text = tags[0].trim()
        if (tags.size > 1) binding.genreLabel.text = tags[1].trim()
        if (tags.size > 2) binding.durationLabel.text = tags[2].trim()

        updateToggleStyles(true)
        showPoster()

        binding.btnPoster.setOnClickListener { 
            updateToggleStyles(true)
            showPoster() 
        }
        binding.btnTrailer.setOnClickListener { 
            updateToggleStyles(false)
            showTrailer() 
        }
    }

    private fun updateToggleStyles(isPoster: Boolean) {
        val activeColor = ContextCompat.getColor(requireContext(), R.color.red_primary)
        val inactiveColor = Color.parseColor("#333333")

        if (isPoster) {
            binding.btnPoster.backgroundTintList = ColorStateList.valueOf(activeColor)
            binding.btnPoster.setTextColor(Color.WHITE)
            binding.btnTrailer.backgroundTintList = ColorStateList.valueOf(inactiveColor)
            binding.btnTrailer.setTextColor(Color.GRAY)
        } else {
            binding.btnTrailer.backgroundTintList = ColorStateList.valueOf(activeColor)
            binding.btnTrailer.setTextColor(Color.WHITE)
            binding.btnPoster.backgroundTintList = ColorStateList.valueOf(inactiveColor)
            binding.btnPoster.setTextColor(Color.GRAY)
        }
    }

    private fun showPoster() {
        VideoPlayer.stop()
        binding.mediaContainer.removeAllViews()
        val imageView = ImageView(requireContext())
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        
        val movie = currentMovie
        if (movie != null) {
            if (!movie.imageUrl.isNullOrEmpty()) {
                Picasso.get().load(movie.imageUrl).into(imageView)
            } else {
                imageView.setImageResource(movie.imageResId)
            }
        }

        binding.mediaContainer.addView(imageView)
    }

    private fun showTrailer() {
        val movie = currentMovie ?: return
        VideoPlayer.attachPlayer(requireContext(), binding.mediaContainer, movie.trailerResId, layoutInflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        VideoPlayer.stop()
        _binding = null
    }
}
