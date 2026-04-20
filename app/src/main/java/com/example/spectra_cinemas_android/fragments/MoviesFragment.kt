package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spectra_cinemas_android.adapters.MoviesAdapter
import com.example.spectra_cinemas_android.databinding.MoviesViewBinding
import com.example.spectra_cinemas_android.utils.MovieData

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

        val movies = MovieData.getMovies()
        val adapter = MoviesAdapter(movies) { movie ->
            // Λογική για κλικ σε ταινία (π.χ. άνοιγμα λεπτομερειών)
        }

        binding.moviesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.moviesRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
