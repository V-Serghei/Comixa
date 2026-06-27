package com.comixa.core.domain.model

data class UserPreferences(
    val isDarkTheme: Boolean = true,
    val readingDirection: ReadingDirection = ReadingDirection.LEFT_TO_RIGHT,
)
