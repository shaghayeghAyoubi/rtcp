package com.example.myapplication.presentation.localization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Language
import com.example.myapplication.presentation.localization.strings.Strings
import com.example.myapplication.presentation.localization.strings.StringsEn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalizationViewModel @Inject constructor(
    private val provider: LocalizationProvider
) : ViewModel() {

    val strings: StateFlow<Strings> = provider.stringsFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        StringsEn // default
    )

    fun switchLanguage(language: Language) {
        viewModelScope.launch {
            provider.changeLanguage(language)
        }
    }
}