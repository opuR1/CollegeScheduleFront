package com.example.collegeschedule.ui.schedule

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import com.example.collegeschedule.data.local.FavoritesManager
import com.example.collegeschedule.data.repository.ScheduleRepository
import com.example.collegeschedule.utils.getWeekDateRange
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val repository: ScheduleRepository,
    private val favoritesManager: FavoritesManager
) : ViewModel() {

    var schedule by mutableStateOf<List<ScheduleByDateDto>>(emptyList())
        private set
    var groups by mutableStateOf<List<String>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var selectedGroup by mutableStateOf("")
        private set

    val favoriteGroups = favoritesManager.favoriteGroups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                groups = repository.getGroups()
                val favorites = favoriteGroups.value.ifEmpty { favoritesManager.favoriteGroups.first() }

                val defaultGroup = if (favorites.isNotEmpty()) favorites.first() else groups.firstOrNull() ?: ""

                if (defaultGroup.isNotEmpty()) {
                    onGroupSelected(defaultGroup)
                }
            } catch (e: Exception) {
                error = "Ошибка загрузки данных"
            }
        }
    }

    fun onGroupSelected(groupName: String) {
        selectedGroup = groupName
        loadSchedule(groupName)
    }

    fun toggleFavorite(groupName: String) {
        if (groupName.isEmpty()) return
        viewModelScope.launch {
            favoritesManager.toggleFavorite(groupName)
        }
    }

    fun loadSchedule(groupName: String) {
        viewModelScope.launch {
            isLoading = true
            error = null
            val (start, end) = getWeekDateRange()
            try {
                schedule = repository.loadSchedule(groupName, start, end)
            } catch (e: Exception) {
                error = "Не удалось загрузить расписание"
            } finally {
                isLoading = false
            }
        }
    }
}