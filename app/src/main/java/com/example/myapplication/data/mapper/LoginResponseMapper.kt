package com.example.myapplication.data.mapper

import com.example.myapplication.data.model.LoginResponseDto
import com.example.myapplication.domain.model.LoginResponse

fun LoginResponseDto.toDomain(): LoginResponse = LoginResponse(
    accessToken = access_token,
    expiresIn = expires_in,
    refreshExpiresIn = refresh_expires_in,
    refreshToken = refresh_token,
    tokenType = token_type,
    idToken = id_token,
    notBeforePolicy = not_before_policy,
    sessionState = session_state,
    scope = scope
)