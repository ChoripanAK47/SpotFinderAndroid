package com.example.spotfinder.repository

import com.example.spotfinder.model.LoginRequest
import com.example.spotfinder.model.LoginResponse
import com.example.spotfinder.model.Usuario
import com.example.spotfinder.net.APIService
import retrofit2.Response

class UsuarioRepository(private val apiService: APIService) {

    suspend fun login(loginRequest: LoginRequest): Response<LoginResponse> {
        return apiService.login(loginRequest)
    }

    suspend fun register(usuario: Usuario): Response<Void> {
        return apiService.register(usuario)
    }
}