package com.example.spectra_cinemas_android.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "halls",
    foreignKeys = [
        ForeignKey(
            entity = Cinema::class,
            parentColumns = ["name"],
            childColumns = ["cinemaName"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cinemaName"])]
)
data class Hall(
    @PrimaryKey val title: String, // π.χ. "Αίθουσα 1 - Συγγρού"
    val cinemaName: String, // Foreign Key
    val imageResId: Int,
    val seats: String,
    val rows: String,
    val exits: String,
    val tech: String
)
