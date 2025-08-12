package com.example.myapplication.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.usecase.GetCameraListUseCase
import com.example.myapplication.domain.usecase.GetRecognizedPeopleUseCase


import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.webrtc.*
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.net.ssl.*

@HiltViewModel
class CameraListViewModel @Inject constructor(
    private val getCameraListUseCase: GetCameraListUseCase,
    private val getRecognizedPeopleUseCase: GetRecognizedPeopleUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(CameraListState())
    val state: StateFlow<CameraListState> = _state

    private val _recognizedPeopleState = MutableStateFlow(RecognizedPeopleState())
    val recognizedPeopleState: StateFlow<RecognizedPeopleState> = _recognizedPeopleState





    init {


        fetchCameras()
        fetchRecognizedPeople()
    }

    private fun fetchCameras() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val cameras = getCameraListUseCase()
                _state.value = _state.value.copy(cameras = cameras, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message ?: "Unknown error")
            }
        }
    }
    private fun fetchRecognizedPeople() {
        viewModelScope.launch {
            _recognizedPeopleState.value = _recognizedPeopleState.value.copy(isLoading = true, error = null)
            try {
                val recognizedPeople = getRecognizedPeopleUseCase(
                    pageNumber = 0,
                    pageSize = 10,
                    sort = "recognizedDate,desc"
                )
                _recognizedPeopleState.value = _recognizedPeopleState.value.copy(recognizedPeople = recognizedPeople, isLoading = false)
            } catch (e: Exception) {
                _recognizedPeopleState.value = _recognizedPeopleState.value.copy(isLoading = false, error = e.message ?: "Unknown error")
            }
        }
    }







}