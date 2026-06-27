package com.comixa.core.domain.repository

import com.comixa.core.domain.model.ReadingDirection
import com.comixa.core.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun get(): Flow<UserPreferences>
    suspend fun setDarkTheme(dark: Boolean)
    suspend fun setReadingDirection(direction: ReadingDirection)
}
