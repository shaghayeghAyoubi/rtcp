package com.example.myapplication.data.repository

import android.util.Log
import com.example.myapplication.data.mapper.toDomain
import com.example.myapplication.data.model.CameraListResponseDto

import com.example.myapplication.data.remote.api.SurveillanceApi
import com.example.myapplication.domain.model.Camera
import com.example.myapplication.domain.repository.SurveillanceRepository
import com.google.gson.Gson
import javax.inject.Inject

class SurveillanceRepositoryImpl @Inject constructor(
    private val api: SurveillanceApi,
) : SurveillanceRepository {

    override suspend fun getCameraList(): List<Camera> {
        val response = api.getCameraList()

        val json = response.body()?.string()
        Log.d("RAW_JSON", json ?: "No content")

        // Optionally convert to object again
        val cameraListResponseDto = Gson().fromJson(json, CameraListResponseDto::class.java)
        return cameraListResponseDto.data.map { it.toDomain() }
    }
}


