package com.example.myapplication.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NolanShimmer(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition()

    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 700f,   // move gradient across
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val nolanShimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFF00FF).copy(alpha = 0.2f),  // purple
            Color(0xFF00E5FF).copy(alpha = 0.4f),  // cyan
            Color(0xFFFF00FF).copy(alpha = 0.2f)   // purple again
        ),
        start = Offset(translate - 300f, 0f),
        end = Offset(translate, 300f)
    )

    Box(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
            .background(nolanShimmerBrush, RoundedCornerShape(12.dp))
    )
}