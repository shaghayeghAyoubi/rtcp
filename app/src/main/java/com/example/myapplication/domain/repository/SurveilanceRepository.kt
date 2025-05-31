package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Camera
import com.example.myapplication.domain.model.StreamResponse

interface SurveillanceRepository {
    suspend fun getCameraList(): List<Camera>
}