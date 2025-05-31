package com.example.myapplication.data.mapper

import com.example.myapplication.data.model.StreamResponseDto
import com.example.myapplication.domain.model.StreamResponse

fun StreamResponseDto.toDomain(): StreamResponse {
    return StreamResponse(
        streamUrl = this.streamUrl,
    )
}
