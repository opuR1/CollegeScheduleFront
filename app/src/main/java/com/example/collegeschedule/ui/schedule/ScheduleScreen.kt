package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.repository.ScheduleRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(repository: ScheduleRepository) {

    val viewModel = remember { ScheduleViewModel(repository) }

    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Выпадающий список с выбором группы
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.selectedGroup,
                onValueChange = {},
                readOnly = true,
                label = { Text("Выберите группу") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                viewModel.groups.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(group) },
                        onClick = {
                            viewModel.onGroupSelected(group)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        // Контент расписания
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when {
                viewModel.isLoading -> CircularProgressIndicator()
                viewModel.error != null -> Text("Ошибка: ${viewModel.error}", color = MaterialTheme.colorScheme.error)
                else -> ScheduleList(viewModel.schedule)
            }
        }
    }
}