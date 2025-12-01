package com.example.spotfinder.network

import com.example.spotfinder.data.model.LoginRequest
import com.example.spotfinder.data.model.LoginResponse
import com.example.spotfinder.data.model.Spot
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("spots")
    suspend fun getSpots(): Response<List<Spot>>

    @Multipart
    @POST("spots")
    suspend fun createSpotMultipart(
        @Part("spot") spotJson: RequestBody,
        @Part files: List<MultipartBody.Part>?
    ): Response<Spot>
}
