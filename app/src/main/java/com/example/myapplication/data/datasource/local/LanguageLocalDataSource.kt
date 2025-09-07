package com.example.myapplication.data.datasource.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.domain.model.Language
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore by preferencesDataStore("settings")

class LanguageLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val LANGUAGE_KEY = stringPreferencesKey("language")

    fun getLanguage(): Flow<Language> {
        return context.dataStore.data.map { prefs ->
            val code = prefs[LANGUAGE_KEY] ?: Language.EN.code
            Language.values().find { it.code == code } ?: Language.EN
        }
    }

    suspend fun saveLanguage(language: Language) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = language.code
        }
    }
}