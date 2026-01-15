package com.example.collegeschedule.ui.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import com.example.collegeschedule.data.repository.ScheduleRepository
import com.example.collegeschedule.utils.getWeekDateRange
import kotlinx.coroutines.launch

class ScheduleViewModel(private val repository: ScheduleRepository) : ViewModel() {

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

    init {
        loadGroups()
    }

    private fun loadGroups() {
        viewModelScope.launch {
            try {
                groups = repository.getGroups()
                if (groups.isNotEmpty()) {
                    onGroupSelected(groups.first())
                }
            } catch (e: Exception) {
                error = "Ошибка загрузки групп: ${e.message}"
            }
        }
    }

    fun onGroupSelected(groupName: String) {
        selectedGroup = groupName
        loadSchedule(groupName)
    }

    fun loadSchedule(groupName: String) {
        viewModelScope.launch {
            isLoading = true
            error = null
            val (start, end) = getWeekDateRange()
            try {
                schedule = repository.loadSchedule(groupName, start, end)
            } catch (e: Exception) {
                error = "Ошибка расписания: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}