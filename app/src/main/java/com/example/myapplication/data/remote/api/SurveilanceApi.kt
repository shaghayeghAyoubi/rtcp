package com.example.myapplication.data.remote.api

import com.example.myapplication.data.model.CameraDto
import com.example.myapplication.data.model.CameraListResponseDto
import com.example.myapplication.data.model.RecognizedPeopleResponseDto
import retrofit2.http.GET
import retrofit2.Response
import okhttp3.ResponseBody
import retrofit2.http.Path
import retrofit2.http.Query

interface SurveillanceApi {

    @GET("api/v1/surveillance/cameras/")
    suspend fun getCameraList(
        @Query("pageNumber") pageNumber: Int = 0,
        @Query("pageSize") pageSize: Int = 12
    ): Response<ResponseBody>

    @GET("api/v1/surveillance/recognized-people/")
    suspend fun getRecognizedPeople(
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
        @Query("sort") sort: String? = null,
        @Query("creationDateFrom") creationDateFrom: String? = null,
        @Query("creationDateTo") creationDateTo: String? = null
    ): Response<RecognizedPeopleResponseDto>
}



