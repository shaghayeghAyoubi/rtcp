package com.example.myapplication.data.repository

import com.example.myapplication.data.mapper.toDomain
import com.example.myapplication.data.mapper.toDto
import com.example.myapplication.data.remote.api.AuthApi
import com.example.myapplication.domain.model.LoginRequest
import com.example.myapplication.domain.model.LoginResponse
import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.domain.repository.TokenRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenRepository: TokenRepository
) : AuthRepository {
    override suspend fun login(request: LoginRequest): LoginResponse {
        val dto = request.toDto()
        val responseDto = api.login(dto)
        val domain = responseDto.toDomain()

        // ✅ Save tokens after successful login
        tokenRepository.saveAccessToken(domain.accessToken)
        domain.refreshToken?.let { tokenRepository.saveRefreshToken(it) }

        return domain
    }
}