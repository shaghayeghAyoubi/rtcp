package com.example.myapplication.di

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.myapplication.data.remote.api.SurveillanceApi
import com.example.myapplication.data.repository.AuthRepositoryImpl
import com.example.myapplication.data.repository.SurveillanceRepositoryImpl
import com.example.myapplication.data.repository.TokenRepositoryImpl
import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.domain.repository.SurveillanceRepository
import com.example.myapplication.domain.repository.TokenRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton




@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSurveillanceRepository(
        impl: SurveillanceRepositoryImpl
    ): SurveillanceRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository


}

