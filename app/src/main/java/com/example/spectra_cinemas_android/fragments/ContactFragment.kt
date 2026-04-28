package com.example.spectra_cinemas_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spectra_cinemas_android.R
import com.example.spectra_cinemas_android.adapters.ContactAdapter
import com.example.spectra_cinemas_android.databinding.ContactViewBinding
import com.example.spectra_cinemas_android.utils.ContactData

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

        val offices = ContactData.getOffices()
        val adapter = ContactAdapter(offices)

        // Χρήση δυναμικού αριθμού στηλών (1 για portrait, 2 για landscape)
        val columns = resources.getInteger(R.integer.contact_columns)
        binding.contactRecyclerView.layoutManager = GridLayoutManager(requireContext(), columns)
        binding.contactRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
