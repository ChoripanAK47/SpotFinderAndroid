package com.example.spotfinder.repository

import com.example.spotfinder.data.model.LoginRequest
import com.example.spotfinder.data.model.LoginResponse
import com.example.spotfinder.network.RetrofitClient
import retrofit2.Response

class UserRepository {
    private val apiService = RetrofitClient.instance

    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return apiService.login(request)
    }
}
