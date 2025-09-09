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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.localization.LanguageSwitcher
import com.example.myapplication.presentation.localization.LocalizationViewModel

@Composable
fun SettingsScreen(
    localizationViewModel: LocalizationViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val strings by localizationViewModel.strings.collectAsState()
    val baseUrl by settingsViewModel.baseUrl.collectAsState("")

    var newUrl by remember { mutableStateOf(baseUrl) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Settings Title
        Text(
            text = strings.changeLanguage, // e.g., "Settings"
            style = MaterialTheme.typography.headlineMedium
        )

        // Language Section
        Text(
            text = strings.changeLanguage, // e.g., "Choose Language"
            style = MaterialTheme.typography.bodyLarge
        )
        LanguageSwitcher(viewModel = localizationViewModel)

        // Base URL Section
        Text(
            text = "API Base URL", // you can localize later
            style = MaterialTheme.typography.bodyLarge
        )

        newUrl?.let {
            OutlinedTextField(
                value = it,
                onValueChange = { newUrl = it },
                label = { Text("Base URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Button(
            onClick = { newUrl?.let { settingsViewModel.saveBaseUrl(it) } },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}