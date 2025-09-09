package com.example.myapplication.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.myapplication.domain.repository.BaseUrlRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BaseUrlRepositoryImpl(
    private val dataStore: DataStore<Preferences> // ✅ Correct type
) : BaseUrlRepository {

    companion object {
        val BASE_URL_KEY = stringPreferencesKey("base_url")
    }

    override suspend fun saveBaseUrl(url: String) {
        dataStore.edit { prefs ->
            prefs[BASE_URL_KEY] = url
        }
    }

    override fun getBaseUrl(): Flow<String?> =
        dataStore.data.map { prefs -> prefs[BASE_URL_KEY] }
}