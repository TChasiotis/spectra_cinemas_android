package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spectra_cinemas_android.adapters.ContactAdapter
import com.example.spectra_cinemas_android.database.AppDatabase
import com.example.spectra_cinemas_android.databinding.ContactViewBinding
import com.example.spectra_cinemas_android.models.Office
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ContactFragment : Fragment() {

    private var _binding: ContactViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ContactViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeOffices()
    }

    private fun observeOffices() {
        val db = AppDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            db.appDao().getAllOfficesLive().collectLatest { list ->
                setupRecyclerView(list)
            }
        }
    }

    private fun setupRecyclerView(offices: List<Office>) {
        val adapter = ContactAdapter(offices)
        binding.contactRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.contactRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
