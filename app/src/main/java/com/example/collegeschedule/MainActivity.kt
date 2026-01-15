package com.example.collegeschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.api.ScheduleApi
import com.example.collegeschedule.data.repository.ScheduleRepository
import com.example.collegeschedule.data.local.FavoritesManager
import com.example.collegeschedule.ui.schedule.ScheduleScreen
import com.example.collegeschedule.ui.schedule.ScheduleViewModel
import com.example.collegeschedule.ui.theme.CollegeScheduleTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val favoritesManager = FavoritesManager(applicationContext)

        setContent {
            CollegeScheduleTheme {
                CollegeScheduleApp(favoritesManager)
            }
        }
    }
}

@Composable
fun CollegeScheduleApp(favoritesManager: FavoritesManager) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5051/") // URL для эмулятора
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api = remember { retrofit.create(ScheduleApi::class.java) }
    val repository = remember { ScheduleRepository(api) }

    // Одна ViewModel на всё приложение для синхронизации данных
    val viewModel = remember { ScheduleViewModel(repository, favoritesManager) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                AppDestinations.entries.forEach { destination ->
                    NavigationBarItem(
                        icon = { Icon(destination.icon, contentDescription = null) },
                        label = { Text(destination.label) },
                        selected = destination == currentDestination,
                        onClick = { currentDestination = destination }
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (currentDestination) {
                AppDestinations.HOME -> ScheduleScreen(viewModel)
                AppDestinations.FAVORITES -> FavoritesScreen(
                    viewModel = viewModel,
                    onGroupClick = { groupName ->
                        viewModel.onGroupSelected(groupName)
                        currentDestination = AppDestinations.HOME // Переключаем на расписание
                    }
                )
                AppDestinations.PROFILE -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Профиль студента")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: ScheduleViewModel,
    onGroupClick: (String) -> Unit
) {
    val favorites by viewModel.favoriteGroups.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Избранные группы", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        if (favorites.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Список избранного пуст", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            LazyColumn {
                items(favorites.toList()) { group ->
                    Card(
                        onClick = { onGroupClick(group) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = group,
                                modifier = Modifier.padding(start = 16.dp).weight(1f),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Icon(Icons.Default.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

enum class AppDestinations(val label: String, val icon: ImageVector) {
    HOME("Главная", Icons.Default.DateRange),
    FAVORITES("Избранное", Icons.Default.Favorite),
    PROFILE("Профиль", Icons.Default.Person),
}