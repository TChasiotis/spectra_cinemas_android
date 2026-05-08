package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spectra_cinemas_android.MainActivity
import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.adapters.MoviesAdapter
import com.example.spectra_cinemas_android.databinding.MoviesViewBinding
import com.example.spectra_cinemas_android.models.Movie
import com.example.spectra_cinemas_android.utils.MovieData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MoviesFragment : Fragment() {

    private var _binding: MoviesViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MoviesViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Χρήση τοπικών δεδομένων για ταχύτητα στην παρουσίαση
        setupRecyclerView(MovieData.getMovies())
    }

    private fun setupRecyclerView(movies: List<Movie>) {
        val mainActivity = (activity as? MainActivity)
        val adapter = MoviesAdapter(movies) { movie ->
            if (mainActivity?.isAdmin == true) {
                mainActivity.replaceFragment(
                    MovieDetailsFragment.newInstance(movie.title),
                    movie.title
                )
            } else {
                (activity as? MainActivity)?.replaceFragment(
                    MovieDetailsFragment.newInstance(movie.title),
                    movie.title
                )
            }
        }

        val columns = resources.getInteger(R.integer.grid_columns)
        binding.moviesRecyclerView.layoutManager = GridLayoutManager(requireContext(), columns)
        binding.moviesRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
