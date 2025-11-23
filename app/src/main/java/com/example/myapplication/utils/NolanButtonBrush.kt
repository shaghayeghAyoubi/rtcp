package com.example.myapplication.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Define Nolan-style gradient Brush
val nolanGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFF00FF), Color(0xFF00E5FF)),
    start = Offset(0f, 0f),
    end = Offset(300f, 300f)
)

@Composable
fun NolanButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(nolanGradient, shape = RoundedCornerShape(12.dp))
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
        )
    }
}
