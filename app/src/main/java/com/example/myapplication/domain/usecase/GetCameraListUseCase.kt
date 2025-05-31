package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Camera
import com.example.myapplication.domain.repository.SurveillanceRepository

class GetCameraListUseCase(
    private val repository: SurveillanceRepository
) {
    suspend operator fun invoke(): List<Camera> {
        return repository.getCameraList()
    }
}