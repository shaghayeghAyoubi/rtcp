package com.example.myapplication.domain.model

data class Camera(
    val id: Int,
    val title: String,
    val rtspURL: String?,
    val rtspURL2: String?,
    val rtspURL3: String?,
    val status: Int,
    val isActive: Boolean,
    val thumbnail: String?,
    val cameraGroups: List<CameraGroup>,
    val pinned: Boolean
)

data class CameraGroup(
    val id: Int,
    val title: String
)