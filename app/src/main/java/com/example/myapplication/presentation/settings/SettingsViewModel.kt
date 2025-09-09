package com.example.myapplication.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.usecase.GetBaseUrlUseCase
import com.example.myapplication.domain.usecase.SaveBaseUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val saveBaseUrlUseCase: SaveBaseUrlUseCase,
    private val getBaseUrlUseCase: GetBaseUrlUseCase
) : ViewModel() {

    val baseUrl = getBaseUrlUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    fun saveBaseUrl(url: String) {
        viewModelScope.launch {
            saveBaseUrlUseCase(url)
        }
    }
}