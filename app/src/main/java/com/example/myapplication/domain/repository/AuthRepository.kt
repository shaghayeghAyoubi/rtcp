package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.LoginRequest
import com.example.myapplication.domain.model.LoginResponse

interface AuthRepository {
    suspend fun login(request: LoginRequest): LoginResponse
}
