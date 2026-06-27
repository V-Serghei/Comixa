package com.comixa.feature.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.comixa.core.domain.model.ReadingDirection
import com.comixa.core.domain.model.UserPreferences
import com.comixa.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : UserPreferencesRepository {

    override fun get(): Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            isDarkTheme = prefs[DARK_THEME] ?: true,
            readingDirection = prefs[READING_DIRECTION]
                ?.let { runCatching { ReadingDirection.valueOf(it) }.getOrNull() }
                ?: ReadingDirection.LEFT_TO_RIGHT,
        )
    }

    override suspend fun setDarkTheme(dark: Boolean) {
        dataStore.edit { it[DARK_THEME] = dark }
    }

    override suspend fun setReadingDirection(direction: ReadingDirection) {
        dataStore.edit { it[READING_DIRECTION] = direction.name }
    }

    private companion object {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val READING_DIRECTION = stringPreferencesKey("reading_direction")
    }
}
