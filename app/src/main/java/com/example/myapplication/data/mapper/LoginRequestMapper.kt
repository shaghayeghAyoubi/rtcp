package com.example.myapplication.data.mapper

import com.example.myapplication.data.model.LoginRequestDto
import com.example.myapplication.domain.model.LoginRequest

fun LoginRequest.toDto(): LoginRequestDto = LoginRequestDto(
    username = username,
    password = password,
    refreshToken = refreshToken
)
fun LoginRequestDto.toDomain(): LoginRequest = LoginRequest(
    username = username,
    password = password,
    refreshToken = refreshToken
)
