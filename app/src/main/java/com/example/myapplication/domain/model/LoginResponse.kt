package com.example.myapplication.domain.model

data class LoginResponse(
    val accessToken: String,
    val expiresIn: Int,
    val refreshExpiresIn: Int,
    val refreshToken: String,
    val tokenType: String,
    val idToken: String,
    val notBeforePolicy: Int,
    val sessionState: String,
    val scope: String
)