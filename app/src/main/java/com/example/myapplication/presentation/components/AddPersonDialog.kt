package com.example.myapplication.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.R

@Composable
fun AddPersonDialog(
    onDismiss: () -> Unit,
    onFinish: (PersonData) -> Unit
) {
    var step by remember { mutableStateOf(1) }

    Dialog(onDismissRequest = onDismiss) {

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1B1E26),
            tonalElevation = 8.dp
        ) {

            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                // --- Step progress indicator ---
                StepHeader(step)

                Spacer(modifier = Modifier.height(20.dp))

                when (step) {
                    1 -> StepUploadImage(onNext = { step = 2 })
                    2 -> StepFillInformation(
                        onBack = { step = 1 },
                        onNext = { step = 3 }
                    )
                    3 -> StepFinish(
                        onBack = { step = 2 },
                        onFinish = { data ->
                            onFinish(data)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StepHeader(step: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = when (step) {
                1 -> "ایجاد فرد جدید"
                2 -> "اطلاعات"
                else -> "تمام"
            },
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
@Composable
fun StepUploadImage(onNext: () -> Unit) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            painter = painterResource(id = R.drawable.ic_add_user),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(120.dp)
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { /* pick image */ },
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Browse")
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(0.6f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B47FF)
            )
        ) {
            Text("بعدی")
        }
    }
}
@Composable
fun StepFillInformation(onBack: () -> Unit, onNext: () -> Unit) {

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("نام") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("توضیحات") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onBack) { Text("قبلی") }
            Button(onClick = onNext) { Text("بعدی") }
        }
    }
}
data class PersonData(
    val name: String,
    val desc: String,
    val imageUri: String?
)

@Composable
fun StepFinish(onBack: () -> Unit, onFinish: (PersonData) -> Unit) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("آیا میخواهید ذخیره کنید؟", color = Color.White, fontSize = 16.sp)

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onBack) { Text("قبلی") }
            Button(onClick = { onFinish(PersonData("name", "desc", null)) }) {
                Text("تمام")
            }
        }
    }
}

