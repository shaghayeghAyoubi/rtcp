package com.example.myapplication.presentation.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Define Nolan-style gradient Brush

@Composable
fun NolanButton(
    text: String,
    onClick: () -> Unit,
    loading: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (loading) {
        // Show shimmer in the same size & shape as the button
        NolanShimmer(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
        )
    } else {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(),
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(NolanGradient, shape = RoundedCornerShape(12.dp))
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp,
            )
        }
    }
}

