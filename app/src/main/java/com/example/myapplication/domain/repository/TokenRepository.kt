package com.example.myapplication.domain.repository
import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    suspend fun saveAccessToken(token: String)
    suspend fun saveRefreshToken(token: String?)

    fun getAccessToken(): Flow<String?>
    fun getRefreshToken(): Flow<String?>
}