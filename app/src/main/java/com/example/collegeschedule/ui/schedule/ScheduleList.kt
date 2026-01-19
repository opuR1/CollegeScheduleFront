package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import java.time.format.DateTimeFormatter
@Composable
fun ScheduleList(data: List<ScheduleByDateDto>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(data) { day ->

            val formattedDate = day.lessonDate.toString().take(10)

            Text(
                text = "$formattedDate (${day.weekday})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )

            if (day.lessons.isEmpty()) {
                Text("Информация отсутствует",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            } else {
                day.lessons.forEach { lesson ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            Text("Пара ${lesson.lessonNumber} (${lesson.time})",
                                style = MaterialTheme.typography.labelLarge)

                            lesson.groupParts.forEach { (part, info) ->
                                if (info != null) {

                                    val prefix = when (part.toString()) {
                                        "SUB1" -> "Подгруппа 1: "
                                        "SUB2" -> "Подгруппа 2: "
                                        else -> ""
                                    }

                                    Text(text = "$prefix${info.subject}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                    Text(info.teacher, style = MaterialTheme.typography.bodySmall)
                                    Text("${info.building}, каб. ${info.classroom}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}