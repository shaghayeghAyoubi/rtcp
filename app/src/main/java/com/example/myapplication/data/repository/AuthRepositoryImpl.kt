package com.example.myapplication.data.repository

import com.example.myapplication.data.mapper.toDomain
import com.example.myapplication.data.mapper.toDto
import com.example.myapplication.data.remote.RetrofitFactory
import com.example.myapplication.data.remote.api.AuthApi
import com.example.myapplication.domain.model.LoginRequest
import com.example.myapplication.domain.model.LoginResponse
import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.domain.repository.BaseUrlRepository
import com.example.myapplication.domain.repository.TokenRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val baseUrlRepository: BaseUrlRepository,
    private val retrofitFactory: RetrofitFactory,
    private val tokenRepository: TokenRepository
) : AuthRepository {

    override suspend fun login(request: LoginRequest): LoginResponse {
        val baseUrl = baseUrlRepository.getBaseUrl().firstOrNull()
            ?: "http://172.15.0.40:7009/"

        val api = retrofitFactory.create(baseUrl).create(AuthApi::class.java)

        val dto = request.toDto()
        val responseDto = api.login(dto)
        val domain = responseDto.toDomain()

        tokenRepository.saveAccessToken(domain.accessToken)
        domain.refreshToken?.let { tokenRepository.saveRefreshToken(it) }

        return domain
    }
}