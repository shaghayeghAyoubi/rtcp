package com.example.myapplication.presentation.event

import com.example.myapplication.domain.model.RecognizedPerson

data class RecognizedPeopleState(
    val recognizedPeople: List<RecognizedPerson> = emptyList(),
    val isLoading: Boolean = false,
    val loadingStream: Boolean = false,
    val streamUrl: String? = null,
    val error: String? = null,
    val streamError: String? = null, // <-- NEW
    val endReached: Boolean = false
    )