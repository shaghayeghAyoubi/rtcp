package com.example.myapplication.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.localization.LanguageSwitcher
import com.example.myapplication.presentation.localization.LocalizationViewModel

@Composable
fun SettingsScreen(
    localizationViewModel: LocalizationViewModel = hiltViewModel()
) {
    val strings by localizationViewModel.strings.collectAsState()

    // Root layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = strings.changeLanguage, // e.g., "Settings"
            style = MaterialTheme.typography.headlineMedium
        )

        // Language switcher section
        Text(
            text = strings.changeLanguage, // e.g., "Choose Language"
            style = MaterialTheme.typography.bodyLarge
        )

        LanguageSwitcher(viewModel = localizationViewModel)

        // Other settings items can go here
        // Example:
        // Row(
        //     verticalAlignment = Alignment.CenterVertically,
        //     horizontalArrangement = Arrangement.SpaceBetween,
        //     modifier = Modifier.fillMaxWidth()
        // ) {
        //     Text(text = strings.darkMode)
        //     Switch(checked = isDarkMode, onCheckedChange = { ... })
        // }
    }
}