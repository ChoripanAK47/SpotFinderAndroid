package com.example.spotfinder.repository

import com.example.spotfinder.data.model.Spot
import com.example.spotfinder.network.RetrofitClient
import retrofit2.Response

class SpotsRepository {
    private val apiService = RetrofitClient.instance

    suspend fun getSpots(): Response<List<Spot>> {
        return apiService.getSpots()
    }

    suspend fun createSpot(spot: Spot): Response<Spot> {
        return apiService.createSpot(spot)
    }
}
