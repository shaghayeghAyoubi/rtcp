package com.example.myapplication.data.model

data class LoginRequestDto(
    val username: String,
    val password: String,
    val refreshToken: String? = null
)