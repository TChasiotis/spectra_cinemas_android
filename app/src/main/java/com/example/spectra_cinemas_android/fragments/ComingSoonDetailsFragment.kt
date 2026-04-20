package com.example.spectra_cinemas_android.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.example.spectra_cinemas_android.databinding.ComingSoonDetailsViewBinding
import com.example.spectra_cinemas_android.models.Movie
import com.example.spectra_cinemas_android.utils.ComingSoonData

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
        binding.releaseDateLabel.text = movie.releaseDate

        val tags = movie.tags.split("|")
        if (tags.size > 0) binding.ratingLabel.text = tags[0].trim()
        if (tags.size > 1) binding.genreLabel.text = tags[1].trim()
        if (tags.size > 2) binding.durationLabel.text = tags[2].trim()

        binding.btnPoster.setOnClickListener { showPoster() }
        binding.btnTrailer.setOnClickListener { showTrailer() }

        showPoster()
    }

    private fun showPoster() {
        binding.mediaContainer.removeAllViews()
        val imageView = ImageView(requireContext())
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        currentMovie?.imageResId?.let { imageView.setImageResource(it) }
        binding.mediaContainer.addView(imageView)
    }

    private fun showTrailer() {
        binding.mediaContainer.removeAllViews()
        val videoView = VideoView(requireContext())
        val uri = Uri.parse("android.resource://" + requireContext().packageName + "/" + (currentMovie?.trailerResId ?: 0))
        videoView.setVideoURI(uri)
        binding.mediaContainer.addView(videoView)
        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            videoView.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
