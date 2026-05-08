package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.adapters.CinemasAdapter
import com.example.spectra_cinemas_android.database.AppDatabase
import com.example.spectra_cinemas_android.databinding.CinemasViewBinding
import com.example.spectra_cinemas_android.models.Cinema
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
        observeCinemas()
    }

    private fun observeCinemas() {
        val db = AppDatabase.getDatabase(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            db.appDao().getAllCinemasLive().collectLatest { list ->
                if (_binding != null) {
                    setupRecyclerView(list)
                }
            }
        }
    }

    private fun setupRecyclerView(cinemas: List<Cinema>) {
        val adapter = CinemasAdapter(cinemas)
        val columns = resources.getInteger(R.integer.cinema_columns)
        binding.cinemasRecyclerView.layoutManager = GridLayoutManager(requireContext(), columns)
        binding.cinemasRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
