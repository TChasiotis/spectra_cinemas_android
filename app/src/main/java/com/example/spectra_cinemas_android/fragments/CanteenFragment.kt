package com.example.spectra_cinemas_android.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.adapters.CanteenAdapter
import com.example.spectra_cinemas_android.database.AppDatabase
import com.example.spectra_cinemas_android.databinding.CanteenViewBinding
import com.example.spectra_cinemas_android.models.Snack
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CanteenFragment : Fragment() {

    private var _binding: CanteenViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CanteenAdapter
    private var allSnacks: List<Snack> = emptyList()
    private var currentType = "SNACK"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CanteenViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeSnacks()
        setupButtons()
    }

    private fun setupRecyclerView() {
        adapter = CanteenAdapter(emptyList())
        binding.canteenRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.canteenRecyclerView.adapter = adapter
    }

    private fun observeSnacks() {
        val db = AppDatabase.getDatabase(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            db.appDao().getCheapSnacksLive(100.0).collectLatest { list ->
                if (_binding != null) {
                    allSnacks = list
                    filterList(currentType)
                }
            }
        }
    }

    private fun setupButtons() {
        binding.btnSnacks.setOnClickListener {
            currentType = "SNACK"
            filterList("SNACK")
            updateFilterUI()
        }
        binding.btnDrinks.setOnClickListener {
            currentType = "DRINK"
            filterList("DRINK")
            updateFilterUI()
        }
        updateFilterUI()
    }

    private fun filterList(type: String) {
        val filtered = allSnacks.filter { it.type == type }
        adapter.updateData(filtered)
    }

    private fun updateFilterUI() {
        val activeColor = ContextCompat.getColor(requireContext(), R.color.red_primary)
        if (currentType == "SNACK") {
            binding.btnSnacks.setBackgroundResource(R.drawable.f_btn_active_background)
            binding.btnSnacks.backgroundTintList = ColorStateList.valueOf(activeColor)
            binding.btnSnacks.setTextColor(Color.WHITE)
            
            binding.btnDrinks.background = null
            binding.btnDrinks.backgroundTintList = null
            binding.btnDrinks.setTextColor(Color.WHITE)
        } else {
            binding.btnDrinks.setBackgroundResource(R.drawable.f_btn_active_background)
            binding.btnDrinks.backgroundTintList = ColorStateList.valueOf(activeColor)
            binding.btnDrinks.setTextColor(Color.WHITE)
            
            binding.btnSnacks.background = null
            binding.btnSnacks.backgroundTintList = null
            binding.btnSnacks.setTextColor(Color.WHITE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
