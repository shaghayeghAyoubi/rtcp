package com.example.myapplication.data.model

import kotlinx.serialization.Serializable


@Serializable
data class FaceRecognitionMessageDto(
    val message: String,
    val createdDate: String,
    val croppedFace: String,
    val nearestNeighbourBiometricId: String?,
    val nearestNeighbourId: String?,
    val nearestNeighbourSimilarity: Double?,
)