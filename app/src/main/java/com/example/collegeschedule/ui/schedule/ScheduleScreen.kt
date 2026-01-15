package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val favorites by viewModel.favoriteGroups.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = viewModel.selectedGroup,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Выберите группу") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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
                            }
                        )
                    }
                }
            }

            IconButton(
                onClick = { viewModel.toggleFavorite(viewModel.selectedGroup) },
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            ) {
                val isFav = favorites.contains(viewModel.selectedGroup)
                Icon(
                    imageVector = if (isFav) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "В избранное",
                    tint = if (isFav) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when {
                viewModel.isLoading -> CircularProgressIndicator()
                viewModel.error != null -> Text("Ошибка: ${viewModel.error}", color = MaterialTheme.colorScheme.error)
                else -> ScheduleList(viewModel.schedule)
            }
        }
    }
}