package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {
    private val _pendingNavigation = MutableSharedFlow<String?>()
    val pendingNavigation = _pendingNavigation.asSharedFlow()

    fun setPendingNavigation(messageId: String?) {
        viewModelScope.launch {
            _pendingNavigation.emit(messageId)
        }
    }
}