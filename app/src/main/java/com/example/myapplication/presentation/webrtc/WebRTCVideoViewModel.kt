package com.example.myapplication.presentation.webrtc

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.usecase.StartWebRTCStreamingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject


@HiltViewModel
class WebRTCVideoViewModel @Inject constructor(
    private val startWebRTCStreamingUseCase: StartWebRTCStreamingUseCase
) : ViewModel() {

    private val _isStreaming = MutableLiveData(false)
    val isStreaming: LiveData<Boolean> = _isStreaming

    fun startStreaming(id: Int, channel: Int, renderer: SurfaceViewRenderer) {
        viewModelScope.launch {
            try {
                _isStreaming.value = true
                startWebRTCStreamingUseCase(id, channel, renderer)
            } catch (e: Exception) {
                Log.e("WebRTCViewModel", "Streaming error: ${e.message}")
                _isStreaming.value = false
            }
        }
    }
}