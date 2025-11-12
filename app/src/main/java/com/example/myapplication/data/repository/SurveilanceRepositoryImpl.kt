package com.example.myapplication.data.repository

import android.util.Log
import com.example.myapplication.data.mapper.toDomain
import com.example.myapplication.data.model.CameraListResponseDto
import com.example.myapplication.data.model.RecognizedPeopleResponseDto
import com.example.myapplication.data.remote.RetrofitFactory

import com.example.myapplication.data.remote.api.SurveillanceApi
import com.example.myapplication.domain.model.Camera
import com.example.myapplication.domain.model.RecognizedPerson
import com.example.myapplication.domain.repository.BaseUrlRepository
import com.example.myapplication.domain.repository.SurveillanceRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SurveillanceRepositoryImpl @Inject constructor(
    private val retrofitFactory: RetrofitFactory,
    private val baseUrlRepository: BaseUrlRepository
) : SurveillanceRepository {

    private suspend fun getApi(): SurveillanceApi {
        val baseUrl = baseUrlRepository.getBaseUrl().firstOrNull()
            ?: "http://172.15.0.60:7009/"
        return retrofitFactory.create(baseUrl).create(SurveillanceApi::class.java)
    }

    override suspend fun getCameraList(): List<Camera> {
        val api = getApi()
        val response = api.getCameraList()

        val json = response.body()?.string()
        Log.d("RAW_JSON", json ?: "No content")

        val cameraListResponseDto = Gson().fromJson(json, CameraListResponseDto::class.java)
        return cameraListResponseDto.data.map { it.toDomain() }
    }

    override suspend fun getRecognizedPeople(
        pageNumber: Int,
        pageSize: Int,
        sort: String?,
        creationDateFrom: String?,
        creationDateTo: String?
    ): List<RecognizedPerson> {
        val api = getApi()
        val response = api.getRecognizedPeople(pageNumber, pageSize, sort, creationDateFrom, creationDateTo)

        if (response.isSuccessful) {
            return response.body()?.toDomain().orEmpty()
        } else {
            throw Exception("Error fetching recognized people: ${response.code()} ${response.message()}")
        }
    }
}

