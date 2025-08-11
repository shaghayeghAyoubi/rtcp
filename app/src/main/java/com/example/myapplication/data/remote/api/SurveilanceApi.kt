package com.example.myapplication.data.remote.api

import com.example.myapplication.data.model.CameraDto
import com.example.myapplication.data.model.CameraListResponseDto
import retrofit2.http.GET
import retrofit2.Response
import okhttp3.ResponseBody
import retrofit2.http.Path
import retrofit2.http.Query

interface SurveillanceApi {
    @GET("api/v1/surveillance/cameras/?pageNumber=0&pageSize=12")
    suspend fun getCameraList(): Response<ResponseBody>
}