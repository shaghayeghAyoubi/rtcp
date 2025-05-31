package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow

class GetAccessTokenUseCase(private val tokenRepository: TokenRepository) {
    operator fun invoke(): Flow<String?> = tokenRepository.getAccessToken()
}