package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.repository.TokenRepository

class SaveAccessTokenUseCase(private val tokenRepository: TokenRepository) {
    suspend operator fun invoke(token: String) {
        tokenRepository.saveAccessToken(token)
    }
}