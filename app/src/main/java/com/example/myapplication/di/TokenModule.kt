package com.example.myapplication.di

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.myapplication.data.repository.TokenRepositoryImpl
import com.example.myapplication.domain.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TokenModule {

    @Provides
    @Singleton
    fun provideTokenRepository(
        @ApplicationContext context: Context
    ): TokenRepository {
        val dataStore = PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("user_prefs")
        }
        return TokenRepositoryImpl(dataStore)
    }
}