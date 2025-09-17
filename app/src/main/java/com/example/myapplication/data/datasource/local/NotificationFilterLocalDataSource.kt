package com.example.myapplication.data.datasource.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.myapplication.domain.model.NotificationFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotificationFilterLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val FILTER_KEY = stringPreferencesKey("notification_filter")

    fun getFilter(): Flow<NotificationFilter> {
        return context.dataStore.data.map { prefs ->
            val key = prefs[FILTER_KEY] ?: NotificationFilter.ALL.key
            NotificationFilter.values().find { it.key == key } ?: NotificationFilter.ALL
        }
    }

    suspend fun saveFilter(filter: NotificationFilter) {
        context.dataStore.edit { prefs ->
            prefs[FILTER_KEY] = filter.key
        }
    }
}