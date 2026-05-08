package com.example.spectra_cinemas_android.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "snacks")
data class Snack(
    @PrimaryKey val name: String = "",
    val price: Double = 0.0,
    val type: String = "", // π.χ. "Food" ή "Drink"
    val imageResId: Int = 0,
    val imageUrl: String? = null
) : Serializable
