package com.example.myapplication.data.mapper


import com.example.myapplication.data.model.RecognizedPeopleResponseDto
import com.example.myapplication.data.model.RecognizedPersonCameraDto
import com.example.myapplication.data.model.RecognizedPersonDto
import com.example.myapplication.domain.model.CameraRecognized
import com.example.myapplication.domain.model.RecognizedPerson

fun RecognizedPersonCameraDto.toDomain(): CameraRecognized {
    return CameraRecognized(
        id = id,
        title = title
    )
}

fun RecognizedPersonDto.toDomain(): RecognizedPerson {
    return RecognizedPerson(
        id = id,
        camera = camera.toDomain(),
        croppedFaceUrl = croppedFace,
        recognizedDate = recognizedDate,
        similarity = nearestNeighbourSimilarity
    )
}

fun RecognizedPeopleResponseDto.toDomain(): List<RecognizedPerson> {
    return content.map { it.toDomain() }
}