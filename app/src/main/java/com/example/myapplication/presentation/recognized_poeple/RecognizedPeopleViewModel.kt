package com.example.myapplication.presentation.recognized_poeple

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.usecase.GetRecognizedPeopleUseCase
import com.example.myapplication.presentation.event.RecognizedPeopleState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecognizedPeopleViewModel @Inject constructor(
    private val getRecognizedPeopleUseCase: GetRecognizedPeopleUseCase
) : ViewModel() {

    private val _recognizedPeopleState = MutableStateFlow(RecognizedPeopleState())
    val recognizedPeopleState: StateFlow<RecognizedPeopleState> = _recognizedPeopleState

    private var currentPage = 0

    init {
        fetchNextPage()
    }

    fun fetchNextPage() {
        viewModelScope.launch {
            // If already loading or reached end → skip
            val currentState = _recognizedPeopleState.value
            if (currentState.isLoading || currentState.endReached) return@launch

            _recognizedPeopleState.value = currentState.copy(isLoading = true, error = null)

            try {
                val newPeople = getRecognizedPeopleUseCase(
                    pageNumber = currentPage,
                    pageSize = 10,
                    sort = "recognizedDate,desc"
                )

                _recognizedPeopleState.value = currentState.copy(
                    recognizedPeople = currentState.recognizedPeople + newPeople,
                    isLoading = false,
                    endReached = newPeople.isEmpty()
                )
                currentPage++
            } catch (e: Exception) {
                _recognizedPeopleState.value = currentState.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }
}
