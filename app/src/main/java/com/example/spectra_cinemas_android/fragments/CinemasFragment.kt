package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.adapters.CinemasAdapter
import com.example.spectra_cinemas_android.databinding.CinemasViewBinding
import com.example.spectra_cinemas_android.utils.CinemaData

class CinemasFragment : Fragment() {

    private var _binding: CinemasViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CinemasViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cinemas = CinemaData.getAllCinemas()
        val adapter = CinemasAdapter(cinemas)
        
        // Χρήση δυναμικού αριθμού στηλών (1 για portrait, 2 για landscape)
        val columns = resources.getInteger(R.integer.cinema_columns)
        binding.cinemasRecyclerView.layoutManager = GridLayoutManager(requireContext(), columns)
        binding.cinemasRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
