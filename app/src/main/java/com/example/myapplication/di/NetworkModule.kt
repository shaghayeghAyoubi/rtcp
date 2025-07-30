package com.example.myapplication.di

import com.example.myapplication.data.remote.api.AuthApi


import com.example.myapplication.data.remote.api.SurveillanceApi
import com.example.myapplication.data.remote.network.TokenInterceptor
import com.example.myapplication.utils.UnsafeOkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


//@Module
//@InstallIn(SingletonComponent::class)
//object NetworkModule {
//
//    private const val BASE_URL = "https://172.15.0.60:7008/"
//
//    @Provides
//    @Singleton
//    fun provideRetrofit(): Retrofit {
//        val okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient()
////            if (BuildConfig.DEBUG) {
////            UnsafeOkHttpClient.getUnsafeOkHttpClient()
////        }
////        else {
////            OkHttpClient.Builder().build()
////        }
//
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideSurveillanceApi(retrofit: Retrofit): SurveillanceApi {
//        return retrofit.create(SurveillanceApi::class.java)
//    }
//}
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://172.15.0.60:7009/"

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenInterceptor: TokenInterceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return UnsafeOkHttpClient.getUnsafeOkHttpClient()
            .newBuilder()
            .addInterceptor(tokenInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }



    @Provides
    @Singleton
    fun provideSurveillanceApi(retrofit: Retrofit): SurveillanceApi {
        return retrofit.create(SurveillanceApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }


}
