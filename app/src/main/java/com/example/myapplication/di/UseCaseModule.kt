package com.example.myapplication.di

import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.domain.repository.SurveillanceRepository
import com.example.myapplication.domain.repository.WebRTCRepository
import com.example.myapplication.domain.usecase.GetCameraListUseCase
import com.example.myapplication.domain.usecase.LoginUseCase
import com.example.myapplication.domain.usecase.StartWebRTCStreamingUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetCameraListUseCase(
        repository: SurveillanceRepository
    ): GetCameraListUseCase {
        return GetCameraListUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideLoginUseCase(
        repository: AuthRepository
    ): LoginUseCase {
        return LoginUseCase(repository)
    }
    // ✅ Add this
    @Provides
    @Singleton
    fun provideStartWebRTCStreamingUseCase(
        repository: WebRTCRepository
    ): StartWebRTCStreamingUseCase {
        return StartWebRTCStreamingUseCase(repository)
    }

}