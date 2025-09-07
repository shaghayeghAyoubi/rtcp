package com.example.myapplication.data.repository

import com.example.myapplication.data.datasource.local.LanguageLocalDataSource
import com.example.myapplication.domain.model.Language
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LanguageRepositoryImpl @Inject constructor(
    private val localDataSource: LanguageLocalDataSource
) {
    fun getLanguage(): Flow<Language> = localDataSource.getLanguage()

    suspend fun saveLanguage(language: Language) = localDataSource.saveLanguage(language)
}