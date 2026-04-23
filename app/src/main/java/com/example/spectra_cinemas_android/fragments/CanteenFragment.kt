package com.example.spectra_cinemas_android.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.adapters.CanteenAdapter
import com.example.spectra_cinemas_android.databinding.CanteenViewBinding
import com.example.spectra_cinemas_android.utils.CanteenData

class CanteenFragment : Fragment() {

    private var _binding: CanteenViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CanteenAdapter

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
        setupButtons()

        // Αρχική εμφάνιση Snacks
        showSnacks()
    }

    private fun setupRecyclerView() {
        adapter = CanteenAdapter(emptyList())
        binding.canteenRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.canteenRecyclerView.adapter = adapter
    }

    private fun setupButtons() {
        binding.btnSnacks.setOnClickListener { showSnacks() }
        binding.btnDrinks.setOnClickListener { showDrinks() }
    }

    private fun showSnacks() {
        updateButtonStyles(isSnacks = true)
        val snacks = CanteenData.getAllSnacks().filter { it.type == "SNACK" }
        adapter.updateData(snacks)
    }

    private fun showDrinks() {
        updateButtonStyles(isSnacks = false)
        val drinks = CanteenData.getAllSnacks().filter { it.type == "DRINK" }
        adapter.updateData(drinks)
    }

    private fun updateButtonStyles(isSnacks: Boolean) {
        val activeColor = ContextCompat.getColor(requireContext(), R.color.red_primary)
        val inactiveColor = Color.TRANSPARENT
        val activeTextColor = Color.WHITE
        val inactiveTextColor = Color.WHITE

        if (isSnacks) {
            binding.btnSnacks.setBackgroundResource(R.drawable.f_btn_active_background)
            binding.btnSnacks.backgroundTintList = android.content.res.ColorStateList.valueOf(activeColor)
            binding.btnSnacks.setTextColor(activeTextColor)

            binding.btnDrinks.setBackgroundResource(0)
            binding.btnDrinks.setBackgroundColor(inactiveColor)
            binding.btnDrinks.setTextColor(inactiveTextColor)
        } else {
            binding.btnDrinks.setBackgroundResource(R.drawable.f_btn_active_background)
            binding.btnDrinks.backgroundTintList = android.content.res.ColorStateList.valueOf(activeColor)
            binding.btnDrinks.setTextColor(activeTextColor)

            binding.btnSnacks.setBackgroundResource(0)
            binding.btnSnacks.setBackgroundColor(inactiveColor)
            binding.btnSnacks.setTextColor(inactiveTextColor)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
