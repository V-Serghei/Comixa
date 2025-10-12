package com.comixa.app.viewmodel.lab4

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class Lab4ViewModel : ViewModel() {

    private val _progress = MutableStateFlow<Int?>(null)
    val progress: StateFlow<Int?> = _progress

    private var taskJob: Job? = null

    fun startTask() {
        if (taskJob?.isActive == true) return
        taskJob = viewModelScope.launch {
            try {
                _progress.value = 0
                for (i in 1..100) {
                    if (!isActive) break
                    delay(50)
                    _progress.value = i
                }
            } finally {
                _progress.value = null
            }
        }
    }

    fun stopTask() {
        taskJob?.cancel()
        _progress.value = null
    }
}
