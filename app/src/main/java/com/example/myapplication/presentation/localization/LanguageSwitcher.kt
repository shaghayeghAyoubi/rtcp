package com.example.myapplication.presentation.localization

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.domain.model.Language


@Composable
fun LanguageSwitcher(viewModel: LocalizationViewModel = hiltViewModel()) {
    val strings by  viewModel.strings.collectAsState()

    DropdownMenuDemo(
        items = listOf(Language.EN, Language.FA),
        onSelect = { viewModel.switchLanguage(it) },
        label = strings.changeLanguage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuDemo(
    items: List<Language>,
    onSelect: (Language) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }  // 👈 fix
    var selected by remember { mutableStateOf(items.first()) } // 👈 fix

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selected.name,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor() // 👈 required for menu positioning
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { language ->
                DropdownMenuItem(
                    text = { Text(language.name) },
                    onClick = {
                        selected = language
                        expanded = false
                        onSelect(language) // notify parent (saves to datastore)
                    }
                )
            }
        }
    }
}