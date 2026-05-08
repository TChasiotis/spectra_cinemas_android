package com.example.spectra_cinemas_android.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offices")
data class Office(
    @PrimaryKey val title: String,
    val subtitle: String,
    val imageResId: Int,
    val address: String,
    val phone: String,
    val email: String
)
