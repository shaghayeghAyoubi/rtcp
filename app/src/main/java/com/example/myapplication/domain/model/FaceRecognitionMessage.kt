package com.example.myapplication.domain.model





data class FaceRecognitionMessage(
    val message: String,
    val cameraTitle : String,
    val createdDate: String,
    val croppedFace: String,
    val nearestNeighbourBiometricId: String?,
    val nearestNeighbourId: String?,
    val nearestNeighbourSimilarity: Double?,

)