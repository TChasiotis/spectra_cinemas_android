package com.example.spectra_cinemas_android.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "cinemas")
data class Cinema(
    @PrimaryKey val name: String = "",
    val city: String = "",
    val address: String = "",
    val phone: String = "",
    val imageResId: Int = 0,
    val imageUrl: String? = null
) : Serializable {
    override fun toString(): String {
        return name
    }
}