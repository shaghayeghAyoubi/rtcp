package com.example.myapplication.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.localization.LanguageSwitcher
import com.example.myapplication.presentation.localization.LocalizationViewModel

@Composable
fun SettingsScreen(
    localizationViewModel: LocalizationViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    isDialog: Boolean = false, // 👈 add this flag
    onSave: (() -> Unit)? = null
) {
    val strings by localizationViewModel.strings.collectAsState()
    val baseUrlFlow = settingsViewModel.baseUrl
    val baseUrlFromStore by baseUrlFlow.collectAsState(initial = null)

    val baseUrl = baseUrlFromStore ?: "http://172.15.0.60:7009/"



    var ip by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }
    // Update ip/port whenever baseUrl changes
    LaunchedEffect(baseUrlFromStore) {
        val match = baseUrlFromStore?.let { Regex("""http://([\d\.]+):(\d+)/""").find(it) }
        ip = match?.groupValues?.getOrNull(1) ?: ""
        port = match?.groupValues?.getOrNull(2) ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = strings.changeLanguage,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = strings.changeLanguage,
            style = MaterialTheme.typography.bodyLarge
        )
        LanguageSwitcher(viewModel = localizationViewModel)

        Text(
            text = "API Base URL",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = baseUrl,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        if (isDialog) {
            // 👇 In dialog → Column design
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = ip,
                    onValueChange = { ip = it },
                    label = { Text("IP") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = port,
                    onValueChange = { port = it },
                    label = { Text("Port") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            // 👇 Default → Row design
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("http://", style = MaterialTheme.typography.bodyLarge)
                OutlinedTextField(
                    value = ip,
                    onValueChange = { ip = it },
                    label = { Text("IP") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Text(":", style = MaterialTheme.typography.bodyLarge)
                OutlinedTextField(
                    value = port,
                    onValueChange = { port = it },
                    label = { Text("Port") },
                    singleLine = true,
                    modifier = Modifier.width(100.dp)
                )
                Text("/", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Button(
            onClick = {
                val finalUrl = "http://$ip:$port/"
                settingsViewModel.saveBaseUrl(finalUrl)
                onSave?.invoke() // 👈 trigger callback if provided
            },
            modifier = if (isDialog) Modifier.align(Alignment.CenterHorizontally) else Modifier.align(Alignment.Start)
        ) {
            Text("Save")
        }
    }
}