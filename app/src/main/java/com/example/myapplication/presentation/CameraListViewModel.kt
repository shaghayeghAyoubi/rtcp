package com.example.myapplication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.usecase.GetCameraListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CameraListViewModel @Inject constructor(
    private val getCameraListUseCase: GetCameraListUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CameraListState())
    val state: StateFlow<CameraListState> = _state

    init {
        fetchCameras()
    }

    private fun fetchCameras() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val cameras = getCameraListUseCase()
                _state.value = _state.value.copy(
                    cameras = cameras,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun fetchCameraStream(cameraId: Int, uuid: Int , channel: Int = 1) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loadingStream = true, error = null)
            try {
                val url = "https://172.15.0.60:8443/stream/$cameraId/channel/$channel/webrtc?uuid=$cameraId&channel=$channel"

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loadingStream = false,
                    error = e.message ?: "Failed to fetch stream"
                )
            }
        }
    }
}