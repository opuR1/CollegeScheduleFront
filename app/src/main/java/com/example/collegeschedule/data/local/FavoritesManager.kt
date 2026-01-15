package com.example.collegeschedule.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "favorites")

class FavoritesManager(private val context: Context) {
    private val FAVORITE_GROUPS_KEY = stringSetPreferencesKey("favorite_groups")

    val favoriteGroups: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[FAVORITE_GROUPS_KEY] ?: emptySet()
        }

    suspend fun toggleFavorite(groupName: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[FAVORITE_GROUPS_KEY] ?: emptySet()
            if (current.contains(groupName)) {
                preferences[FAVORITE_GROUPS_KEY] = current - groupName
            } else {
                preferences[FAVORITE_GROUPS_KEY] = current + groupName
            }
        }
    }
}