package com.example.myapplication.presentation

import com.example.myapplication.domain.model.Camera

data class CameraListState(
    val cameras: List<Camera> = emptyList(),
    val isLoading: Boolean = true,
    val loadingStream: Boolean = false,
    val streamUrl: String? = null,
    val error: String? = null,
    val streamError: String? = null // <-- NEW
)