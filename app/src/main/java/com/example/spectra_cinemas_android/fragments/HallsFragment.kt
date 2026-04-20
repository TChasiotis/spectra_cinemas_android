package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spectra_cinemas_android.databinding.HallsViewBinding
import com.example.spectra_cinemas_android.databinding.HallItemBinding
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
        
        binding.hallsContainer.removeAllViews()
        
        for (hall in halls) {
            val itemBinding = HallItemBinding.inflate(layoutInflater, binding.hallsContainer, false)
            
            itemBinding.hallTitle.text = hall.title
            itemBinding.hallSpecs.text = "💺 ${hall.seats} | 📏 ${hall.rows} | 🚪 ${hall.exits}"
            itemBinding.hallTech.text = "🛠 ${hall.tech}"
            itemBinding.hallImage.setImageResource(hall.imageResId)
            
            binding.hallsContainer.addView(itemBinding.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
