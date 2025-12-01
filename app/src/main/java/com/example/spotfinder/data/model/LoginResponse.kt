package com.example.spotfinder.data.model

// Ajustado para reflejar la respuesta del backend (devuelve solo token normalmente)
data class LoginResponse(
    val token: String,
    val id: Long? = null,
    val name: String? = null,
    val email: String? = null
)
