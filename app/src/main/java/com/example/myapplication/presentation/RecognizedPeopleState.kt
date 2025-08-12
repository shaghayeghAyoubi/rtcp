package com.example.myapplication.presentation

import com.example.myapplication.domain.model.Camera
import com.example.myapplication.domain.model.RecognizedPerson

data class RecognizedPeopleState(
    val recognizedPeople: List<RecognizedPerson> = emptyList(),
    val isLoading: Boolean = true,
    val loadingStream: Boolean = false,
    val streamUrl: String? = null,
    val error: String? = null,
    val streamError: String? = null // <-- NEW
)