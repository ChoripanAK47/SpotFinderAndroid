package com.example.spotfinder.repository

import com.example.spotfinder.data.model.Spot
import com.example.spotfinder.net.APIService
import retrofit2.Response

class SpotRepository(private val apiService: APIService) {

    suspend fun getSpots(): Response<List<Spot>> {
        return apiService.getSpots()
    }

    suspend fun createSpot(spot: Spot): Response<Spot> {
        return apiService.createSpot(spot)
    }

    suspend fun deleteSpot(id: Long): Response<Void> {
        return apiService.deleteSpot(id)
    }
}