package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.LoginRequest
import com.example.myapplication.domain.model.LoginResponse
import com.example.myapplication.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = repository.login(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}