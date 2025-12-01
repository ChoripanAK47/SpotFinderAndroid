package com.example.spotfinder.net

import com.example.spotfinder.data.model.LoginRequest
import com.example.spotfinder.data.model.LoginResponse
import com.example.spotfinder.data.model.Spot
import com.example.spotfinder.model.Usuario
import retrofit2.Response
import retrofit2.http.*

interface APIService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body usuario: Usuario): Response<Void>

    @GET("spots")
    suspend fun getSpots(): Response<List<Spot>>

    @POST("spots")
    suspend fun createSpot(@Body spot: Spot): Response<Spot>

    @DELETE("spots/{id}")
    suspend fun deleteSpot(@Path("id") id: Long): Response<Void>
}