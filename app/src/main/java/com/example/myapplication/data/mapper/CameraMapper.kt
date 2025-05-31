package com.example.myapplication.data.mapper

import com.example.myapplication.data.model.CameraDto
import com.example.myapplication.data.model.CameraGroupDto
import com.example.myapplication.domain.model.Camera
import com.example.myapplication.domain.model.CameraGroup

fun CameraDto.toDomain(): Camera {
    return Camera(
        id = id,
        title = title,
        rtspURL = rtspURL,
        rtspURL2 = rtspURL2,
        rtspURL3 = rtspURL3,
        status = status,
        isActive = activation,
        thumbnail = thumbnail,
        cameraGroups = cameraGroups.map { it.toDomain() },
        pinned = pinned
    )
}

fun CameraGroupDto.toDomain(): CameraGroup {
    return CameraGroup(
        id = id,
        title = title
    )
}