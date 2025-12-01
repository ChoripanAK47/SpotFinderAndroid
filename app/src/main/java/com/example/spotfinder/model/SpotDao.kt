package com.example.spotfinder.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.spotfinder.data.model.Spot
import kotlin.jvm.JvmSuppressWildcards

@Dao
@JvmSuppressWildcards
interface SpotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpot(spot: Spot): Long

    @Query("SELECT * FROM spots ORDER BY name ASC")
    fun getAllSpots(): Flow<List<Spot>>

    @Query("SELECT * FROM spots WHERE id = :id")
    fun getSpotById(id: Long): Flow<Spot?>

    @Query("SELECT * FROM spots WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchSpots(query: String): Flow<List<Spot>>

    @Delete
    suspend fun delete(spot: Spot): Int

}