package com.example.myapplication.data.model


// data/remote/dto/RecognizedPeopleResponseDto.kt
data class RecognizedPeopleResponseDto(
    val content: List<RecognizedPersonDto>
)

data class RecognizedPersonDto(
    val id: Long,
    val camera: RecognizedPersonCameraDto,
    val croppedFace: String,
    val recognizedDate: String,
    val nearestNeighbourSimilarity: Double
)

data class RecognizedPersonCameraDto(
    val id: Long,
    val title: String
)
