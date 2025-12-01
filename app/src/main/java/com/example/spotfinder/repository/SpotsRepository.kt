package com.example.spotfinder.repository

import com.example.spotfinder.data.model.Spot
import com.example.spotfinder.network.RetrofitClient
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.Response

class SpotsRepository {
    private val apiService = RetrofitClient.instance

    suspend fun getSpots(): Response<List<Spot>> {
        return apiService.getSpots()
    }

    suspend fun createSpotMultipart(spotJson: RequestBody, files: List<MultipartBody.Part>?): Response<Spot> {
        return apiService.createSpotMultipart(spotJson, files)
    }

    // Compat helper: serializa Spot a JSON y llama a multipart sin archivos
    suspend fun createSpot(spot: Spot): Response<Spot> {
        val gson = Gson()
        val json = gson.toJson(mapOf(
            "nombre" to spot.name,
            "descripcion" to spot.description,
            "ubicacion" to mapOf("lat" to spot.latitude, "lng" to spot.longitude),
            "comuna" to "",
            "servicios" to mapOf("tieneBanos" to false, "tieneZonasRecreativas" to false, "tieneComercioCercano" to false)
        ))
        val body = json.toRequestBody("application/json".toMediaTypeOrNull())
        return apiService.createSpotMultipart(body, null)
    }
}
