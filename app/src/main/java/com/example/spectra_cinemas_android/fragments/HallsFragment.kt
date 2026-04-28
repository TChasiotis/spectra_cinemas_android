package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.adapters.HallsAdapter
import com.example.spectra_cinemas_android.databinding.HallsViewBinding
import com.example.spectra_cinemas_android.utils.HallData

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

        val halls = HallData.getHalls()
        val adapter = HallsAdapter(halls)
        
        val columns = resources.getInteger(R.integer.hall_columns)
        binding.hallsRecyclerView.layoutManager = GridLayoutManager(requireContext(), columns)
        binding.hallsRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
