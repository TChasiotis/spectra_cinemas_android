package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spectra_cinemas_android.databinding.CinemasViewBinding
import com.example.spectra_cinemas_android.databinding.CinemaItemBinding
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

        // Γεμίζουμε το container δυναμικά, χωρίς RecyclerView
        val cinemas = CinemaData.getAllCinemas()
        
        binding.cinemasContainer.removeAllViews()
        
        for (cinema in cinemas) {
            val itemBinding = CinemaItemBinding.inflate(layoutInflater, binding.cinemasContainer, false)
            
            itemBinding.cinemaName.text = cinema.name
            itemBinding.cinemaCity.text = "Πόλη: ${cinema.city}"
            itemBinding.cinemaAddress.text = "📍 ${cinema.address}"
            itemBinding.cinemaPhone.text = "📞 ${cinema.phone}"
            itemBinding.cinemaImage.setImageResource(cinema.imageResId)
            
            binding.cinemasContainer.addView(itemBinding.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
