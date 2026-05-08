package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spectra_cinemas_android.adapters.HallsAdapter
import com.example.spectra_cinemas_android.database.AppDatabase
import com.example.spectra_cinemas_android.databinding.HallsViewBinding
import com.example.spectra_cinemas_android.models.Hall
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HallsFragment : Fragment() {

    private var _binding: HallsViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HallsViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeHalls()
    }

    private fun observeHalls() {
        val db = AppDatabase.getDatabase(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            db.appDao().getAllHallsLive().collectLatest { list ->
                if (_binding != null) {
                    setupRecyclerView(list)
                }
            }
        }
    }

    private fun setupRecyclerView(halls: List<Hall>) {
        val adapter = HallsAdapter(halls)
        binding.hallsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.hallsRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
