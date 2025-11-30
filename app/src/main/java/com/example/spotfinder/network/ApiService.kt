package com.example.spotfinder.network

import com.example.spotfinder.data.model.LoginRequest
import com.example.spotfinder.data.model.LoginResponse
import com.example.spotfinder.data.model.Spot
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("spots")
    suspend fun getSpots(): Response<List<Spot>>

    @POST("spots")
    suspend fun createSpot(@Body spot: Spot): Response<Spot>
}
