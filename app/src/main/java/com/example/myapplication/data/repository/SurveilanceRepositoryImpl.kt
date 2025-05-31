package com.example.myapplication.data.repository

import com.example.myapplication.data.mapper.toDomain

import com.example.myapplication.data.remote.api.SurveillanceApi
import com.example.myapplication.domain.model.Camera
import com.example.myapplication.domain.model.StreamResponse
import com.example.myapplication.domain.repository.SurveillanceRepository
import javax.inject.Inject

class SurveillanceRepositoryImpl @Inject constructor(
    private val api: SurveillanceApi,
) : SurveillanceRepository {
    override suspend fun getCameraList(): List<Camera> {
        return api.getCameraList().map { it.toDomain() }
    }

}


