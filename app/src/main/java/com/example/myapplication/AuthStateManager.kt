package com.example.myapplication

import com.example.myapplication.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStateManager @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    suspend fun isUserLoggedIn(): Boolean {
        return tokenRepository.getAccessToken().firstOrNull() != null
    }

    // For composables that need to observe login state
    fun getLoginState(): Flow<Boolean> {
        return tokenRepository.getAccessToken().map { it != null }
    }
}