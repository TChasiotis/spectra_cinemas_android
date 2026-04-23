package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spectra_cinemas_android.adapters.HistoryAdapter
import com.example.spectra_cinemas_android.databinding.HistoryViewBinding
import com.example.spectra_cinemas_android.utils.HistoryManager

class HistoryFragment : Fragment() {

    private var _binding: HistoryViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HistoryViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.historyList.layoutManager = LinearLayoutManager(requireContext())
        
        val tickets = HistoryManager.getHistory()
        val adapter = HistoryAdapter(tickets) { ticket ->
            // Εδώ στο JavaFX άνοιγε το BookingFinalView. 
            // Στο Android θα εμφανίσουμε ένα Toast προς το παρόν ή θα ανοίξουμε το αντίστοιχο Fragment.
            Toast.makeText(requireContext(), "Επιλέχθηκε: ${ticket.movieTitle}", Toast.LENGTH_SHORT).show()
        }
        binding.historyList.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
