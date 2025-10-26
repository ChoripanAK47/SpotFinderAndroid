package com.example.spotfinder.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spots")
data class Spot(
    @PrimaryKey(autoGenerate = true)
    val idSpot: Int = 0, // Room autogenerará el ID

    @ColumnInfo(name = "nombre_spot")
    val nombreSpot: String,

    @ColumnInfo(name = "descripcion_spot")
    val descripcionSpot: String,

    @ColumnInfo(name = "comuna_spot")
    val comunaSpot: String,

    @ColumnInfo(name = "ubicacion_spot")
    val ubicacionSpot: String,

    @ColumnInfo(name = "image_url")
    var imageUrl: String // Cambiado a String para guardar la URI/URL de la imagen
)