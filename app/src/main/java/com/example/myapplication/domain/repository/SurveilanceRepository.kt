package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Camera
import com.example.myapplication.domain.model.RecognizedPerson

interface SurveillanceRepository {
    suspend fun getCameraList(): List<Camera>

    suspend fun getRecognizedPeople(
        pageNumber: Int,
        pageSize: Int,
        sort: String? = null,
        creationDateFrom: String? = null,
        creationDateTo: String? = null
    ): List<RecognizedPerson>
}

