package com.example.myapplication.data.remote.api


import com.example.myapplication.data.model.LoginRequestDto
import com.example.myapplication.data.model.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/security/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto
}
