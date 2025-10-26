package com.example.spotfinder.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SpotDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertSpot(spot: Spot)

    @Query("SELECT * FROM spots ORDER BY nombre_spot ASC")
    fun getAllSpots(): Flow<List<Spot>>

    @Query("SELECT * FROM spots WHERE idSpot = :id")
    fun getSpotById(id: Int): Flow<Spot>

    @Query("SELECT * FROM spots WHERE nombre_spot LIKE '%' || :query || '%' OR comuna_spot LIKE '%' || :query || '%'")
    fun searchSpots(query: String): Flow<List<Spot>>
}