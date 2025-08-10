package com.example.myapplication.data.model

import com.google.gson.annotations.SerializedName

data class CameraListResponseDto(
    @SerializedName("content")
    val data: List<CameraDto>,

    @SerializedName("totalElements")
    val totalCount: Int
)
data class CameraDto(
    val id: Int,
    val title: String,
    val rtspURL: String?,
    val rtspURL2: String?,
    val rtspURL3: String?,
    val status: Int,
    val activation: Boolean,
    val thumbnail: String?,
    val cameraGroups: List<CameraGroupDto>,
    val pinned: Boolean
)

data class CameraGroupDto(
    val id: Int,
    val createdBy: String,
    val createdDate: String,
    val lastUpdatedBy: String,
    val lastUpdateDate: String,
    val title: String
)