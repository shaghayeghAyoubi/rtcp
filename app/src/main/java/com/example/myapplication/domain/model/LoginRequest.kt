package com.example.myapplication.domain.model

data class LoginRequest(
    val username: String,
    val password: String,
    val refreshToken: String? = null
)