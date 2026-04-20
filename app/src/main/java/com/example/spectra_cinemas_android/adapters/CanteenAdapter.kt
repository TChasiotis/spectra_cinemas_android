package com.example.spectra_cinemas_android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spectra_cinemas_android.databinding.SnackItemBinding
import com.example.spectra_cinemas_android.models.Snack

class CanteenAdapter(
    private var snacks: List<Snack>
) : RecyclerView.Adapter<CanteenAdapter.SnackViewHolder>() {

    inner class SnackViewHolder(private val binding: SnackItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(snack: Snack) {
            binding.snackName.text = snack.name
            binding.snackPrice.text = String.format("%.2f€", snack.price)
            binding.snackImage.setImageResource(snack.imageResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnackViewHolder {
        val binding = SnackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SnackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SnackViewHolder, position: Int) {
        holder.bind(snacks[position])
    }

    override fun getItemCount(): Int = snacks.size

    fun updateData(newSnacks: List<Snack>) {
        this.snacks = newSnacks
        notifyDataSetChanged()
    }
}
