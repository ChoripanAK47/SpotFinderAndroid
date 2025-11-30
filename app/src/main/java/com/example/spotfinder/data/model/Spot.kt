package com.example.spotfinder.data.model

data class Spot(
    val id: Long,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String? = null,
    val userId: Long
)
