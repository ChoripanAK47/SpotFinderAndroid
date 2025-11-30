package com.example.spotfinder.data.model

data class LoginResponse(
    val id: Long,
    val name: String,
    val email: String,
    val token: String
)
