package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Camera

interface SurveillanceRepository {
    suspend fun getCameraList(): List<Camera>
}