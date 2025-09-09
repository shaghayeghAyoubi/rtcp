package com.example.myapplication.di
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.myapplication.data.datasource.local.LanguageLocalDataSource
import com.example.myapplication.data.repository.BaseUrlRepositoryImpl
import com.example.myapplication.data.repository.TokenRepositoryImpl
import com.example.myapplication.domain.repository.BaseUrlRepository
import com.example.myapplication.domain.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("user_prefs")
        }
    }

    @Provides
    @Singleton
    fun provideLanguageLocalDataSource(@ApplicationContext context: Context): LanguageLocalDataSource {
        return LanguageLocalDataSource(context)
    }

    @Provides
    @Singleton
    fun provideTokenRepository(
        dataStore: DataStore<Preferences>
    ): TokenRepository {
        return TokenRepositoryImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideBaseUrlRepository(
        dataStore: DataStore<Preferences>
    ): BaseUrlRepository {
        return BaseUrlRepositoryImpl(dataStore)
    }
}