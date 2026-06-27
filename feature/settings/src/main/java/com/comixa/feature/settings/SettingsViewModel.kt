package com.comixa.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comixa.core.domain.model.ReadingDirection
import com.comixa.core.domain.model.UserPreferences
import com.comixa.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsRepository: UserPreferencesRepository,
) : ViewModel() {

    val uiState: StateFlow<UserPreferences> = prefsRepository.get()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPreferences(),
        )

    fun setDarkTheme(dark: Boolean) {
        viewModelScope.launch { prefsRepository.setDarkTheme(dark) }
    }

    fun setReadingDirection(direction: ReadingDirection) {
        viewModelScope.launch { prefsRepository.setReadingDirection(direction) }
    }
}
