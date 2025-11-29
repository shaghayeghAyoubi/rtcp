package com.example.myapplication.presentation.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun GradientIcon(resId: Int, selected: Boolean) {

    val transparentBrush = remember {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent)
        )
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .background(
                brush = if (selected) NolanGradient else transparentBrush,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            colorFilter = if (selected) {
                ColorFilter.tint(Color.White)
            } else {
                null   // 🔥 No tint → icon keeps original colors
            }
        )
    }
}

