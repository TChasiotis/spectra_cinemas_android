package com.example.spectra_cinemas_android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spectra_cinemas_android.databinding.ContactItemBinding
import com.example.spectra_cinemas_android.models.Office

class ContactAdapter(
    private val offices: List<Office>
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(private val binding: ContactItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(office: Office) {
            binding.officeTitle.text = office.title
            binding.officeSubtitle.text = office.subtitle
            binding.officeAddress.text = "📍 ${office.address}"
            binding.officePhone.text = "📞 ${office.phone}"
            binding.officeEmail.text = "📧 ${office.email}"
            binding.officeImage.setImageResource(office.imageResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(offices[position])
    }

    override fun getItemCount(): Int = offices.size
}
