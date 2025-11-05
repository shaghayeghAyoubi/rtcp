package com.example.myapplication.domain.model

data class RecognizedPerson(
    val id: Long,
    val camera: CameraRecognized,
    val croppedFaceUrl: String,
    val recognizedDate: String? = null,
    val similarity: Double
)

// domain/model/CameraRecognized.kt
data class CameraRecognized(
    val id: Long,
    val title: String
)