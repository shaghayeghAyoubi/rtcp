package com.example.myapplication.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.datasource.local.NotificationFilterLocalDataSource
import com.example.myapplication.domain.model.NotificationFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val filterLocalDataSource: NotificationFilterLocalDataSource
) : ViewModel() {

    val notificationFilter: StateFlow<NotificationFilter> =
        filterLocalDataSource.getFilter()
            .stateIn(viewModelScope, SharingStarted.Lazily, NotificationFilter.ALL)

    fun updateFilter(filter: NotificationFilter) {
        viewModelScope.launch {
            filterLocalDataSource.saveFilter(filter)
        }
    }
}