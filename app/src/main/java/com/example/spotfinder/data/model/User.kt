package com.example.spotfinder.data.model

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val token: String? = null
)
