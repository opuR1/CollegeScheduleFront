package com.example.collegeschedule.data.repository

import com.example.collegeschedule.data.api.ScheduleApi
import com.example.collegeschedule.data.dto.ScheduleByDateDto

class ScheduleRepository(private val api: ScheduleApi) {
    suspend fun loadSchedule(group: String, start: String, end: String): List<ScheduleByDateDto> {
        return api.getSchedule(
            groupName = group,
            start = start,
            end = end
        )
    }

    suspend fun getGroups(): List<String> {
        return api.getAllGroups()
    }
}