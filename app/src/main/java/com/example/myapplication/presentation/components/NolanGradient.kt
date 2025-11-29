package com.example.myapplication.presentation.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Define Nolan-style gradient Brush
val NolanGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFF00FF), Color(0xFF00E5FF)),
    start = Offset(0f, 0f),
    end = Offset(300f, 300f)
)