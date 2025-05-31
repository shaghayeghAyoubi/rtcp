package com.example.myapplication.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.myapplication.domain.repository.TokenRepository

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : TokenRepository {

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    override suspend fun saveAccessToken(token: String) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = token
        }
    }

    override suspend fun saveRefreshToken(token: String?) {
        dataStore.edit { prefs ->
            if (token == null) {
                prefs.remove(REFRESH_TOKEN)
            } else {
                prefs[REFRESH_TOKEN] = token
            }
        }
    }

    override fun getAccessToken(): Flow<String?> = dataStore.data
        .map { prefs -> prefs[ACCESS_TOKEN] }

    override fun getRefreshToken(): Flow<String?> = dataStore.data
        .map { prefs -> prefs[REFRESH_TOKEN] }
}