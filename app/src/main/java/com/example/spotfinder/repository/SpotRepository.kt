package com.example.spotfinder.repository

import com.example.spotfinder.model.SpotDao
import com.example.spotfinder.model.Spot
import kotlinx.coroutines.flow.Flow

class SpotRepository(private val spotDao: SpotDao) {

    val allSpots: Flow<List<Spot>> = spotDao.getAllSpots()

    suspend fun insert(spot: Spot) {
        spotDao.insertSpot(spot)
    }

    fun searchSpots(query: String): Flow<List<Spot>> {
        return spotDao.searchSpots(query)
    }

    fun getSpotById(id: Int): Flow<Spot> {
        return spotDao.getSpotById(id)
    }
}