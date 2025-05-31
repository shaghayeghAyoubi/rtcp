package com.example.myapplication.data.remote.api

import com.example.myapplication.data.model.CameraDto
import com.example.myapplication.data.model.StreamResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SurveillanceApi {
    @GET("api/v1/surveillance/cameras/list")
    suspend fun getCameraList(): List<CameraDto>
}
