package com.example.spectra_cinemas_android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spectra_cinemas_android.databinding.BookingSnackItemBinding
import com.example.spectra_cinemas_android.models.Snack
import java.util.Locale

class BookingCanteenAdapter(
    private var snacks: List<Snack>,
    private val cart: MutableMap<Snack, Int>,
    private val onCartUpdated: () -> Unit
) : RecyclerView.Adapter<BookingCanteenAdapter.BookingSnackViewHolder>() {

    inner class BookingSnackViewHolder(private val binding: BookingSnackItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(snack: Snack) {
            binding.snackName.text = snack.name
            binding.snackPrice.text = String.format(Locale.getDefault(), "%.2f€", snack.price)
            binding.snackImage.setImageResource(snack.imageResId)

            val qty = cart[snack] ?: 0
            binding.snackQuantity.text = qty.toString()

            binding.btnPlus.setOnClickListener {
                val newQty = (cart[snack] ?: 0) + 1
                cart[snack] = newQty
                binding.snackQuantity.text = newQty.toString()
                onCartUpdated()
            }

            binding.btnMinus.setOnClickListener {
                val current = cart[snack] ?: 0
                if (current > 0) {
                    val newQty = current - 1
                    if (newQty == 0) cart.remove(snack) else cart[snack] = newQty
                    binding.snackQuantity.text = newQty.toString()
                    onCartUpdated()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingSnackViewHolder {
        val binding = BookingSnackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingSnackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingSnackViewHolder, position: Int) {
        holder.bind(snacks[position])
    }

    override fun getItemCount(): Int = snacks.size

    fun updateData(newSnacks: List<Snack>) {
        this.snacks = newSnacks
        notifyDataSetChanged()
    }
}
