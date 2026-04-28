package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spectra_cinemas_android.MainActivity
import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.adapters.ComingSoonAdapter
import com.example.spectra_cinemas_android.databinding.ComingSoonViewBinding
import com.example.spectra_cinemas_android.utils.ComingSoonData

class ComingSoonFragment : Fragment() {

    private var _binding: ComingSoonViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ComingSoonViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movies = ComingSoonData.getComingSoonMovies()
        val adapter = ComingSoonAdapter(movies) { movie ->
            (activity as? MainActivity)?.replaceFragment(
                ComingSoonDetailsFragment.newInstance(movie.title),
                "Λεπτομέρειες"
            )
        }

        val columns = resources.getInteger(R.integer.grid_columns)
        binding.comingSoonRecyclerView.layoutManager = GridLayoutManager(requireContext(), columns)
        binding.comingSoonRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
