package com.comixa.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comixa.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val prefsRepository: UserPreferencesRepository,
) : ViewModel() {

    val isDarkTheme: StateFlow<Boolean> = prefsRepository.get()
        .map { it.isDarkTheme }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    fun toggleTheme() {
        viewModelScope.launch {
            prefsRepository.setDarkTheme(!isDarkTheme.value)
        }
    }
}
