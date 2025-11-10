package com.example.myapplication.data.mapper


import com.example.myapplication.data.model.RecognizedPeopleResponseDto
import com.example.myapplication.data.model.RecognizedPersonCameraDto
import com.example.myapplication.data.model.RecognizedPersonDto
import com.example.myapplication.domain.model.CameraRecognized
import com.example.myapplication.domain.model.RecognizedPerson

fun RecognizedPersonCameraDto.toDomain(): CameraRecognized {
    return CameraRecognized(
        id = id ?: -1L,                // provide safe default
        title = title ?: "Unknown"     // avoid null crash
    )
}

fun RecognizedPersonDto.toDomain(): RecognizedPerson {
    return RecognizedPerson(
        id = id,
        camera = camera.toDomain(),
        croppedFaceUrl = croppedFace,
        recognizedDate = recognizedDate ?: "Unknown Date",
        similarity = nearestNeighbourSimilarity
    )
}

fun RecognizedPeopleResponseDto.toDomain(): List<RecognizedPerson> {
    return content.map { it.toDomain() }
}