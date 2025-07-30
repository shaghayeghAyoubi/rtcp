package com.example.myapplication.data.mapper

import com.example.myapplication.data.model.FaceRecognitionMessageDto
import com.example.myapplication.domain.model.FaceRecognitionMessage


fun FaceRecognitionMessageDto.toDomain():  FaceRecognitionMessage = FaceRecognitionMessage(
    message = message,
    createdDate = createdDate,
    croppedFace = croppedFace,
    nearestNeighbourBiometricId = nearestNeighbourBiometricId,
    nearestNeighbourId = nearestNeighbourId,
    nearestNeighbourSimilarity = nearestNeighbourSimilarity,

)
