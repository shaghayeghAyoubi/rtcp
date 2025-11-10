package com.example.myapplication.data.model

import kotlinx.serialization.Serializable


@Serializable
data class FaceRecognitionMessageDto(
    val message: String,
    val cameraTitle: String,
    val cameraId: Int? = null, // ✅ New optional field
    val createdDate: String,
    val croppedFace: String?,
    val nearestNeighbourSimilarity: Double? = null,
    val nearestNeighbourBiometricId: String? = null,
    val nearestNeighbourId: String? = null
)