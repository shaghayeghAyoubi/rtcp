package com.example.myapplication.domain.repository

import kotlinx.coroutines.flow.Flow

interface BaseUrlRepository {
    suspend fun saveBaseUrl(url: String)
    fun getBaseUrl(): Flow<String?>
}