package com.example.spectra_cinemas_android.models

import androidx.room.Entity
import android.provider.BaseColumns
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey val title: String = "",
    val englishTitle: String = "",
    val description: String = "",
    val imageResId: Int = 0,
    val trailerResId: Int = 0,
    val tags: String = "",
    val showtimes: List<String>? = null,
    val releaseDate: String? = null,
    val isComingSoon: Boolean = false,
    val imageUrl: String? = null
) : Serializable
