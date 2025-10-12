package com.comixa.app.viewmodel.lab3

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.comixa.data.rss.ProgressReporter
import com.comixa.data.rss.RssRepository
import com.comixa.data.rss.SourceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class Lab3SourcesViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = RssRepository(app)

    private val _sources = MutableStateFlow<List<SourceEntity>>(emptyList())
    val sources: StateFlow<List<SourceEntity>> = _sources

    private val _progress = MutableStateFlow<Int?>(null)
    val progress: StateFlow<Int?> = _progress

    init { reload() }

    fun add(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.addSourceAndSyncWithProgress(url, reporter())
            } finally {
                reload()
            }
        }
    }

    fun refresh(sourceId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.refreshSourceWithProgress(sourceId, reporter())
            } finally {
                reload()
            }
        }
    }

    fun delete(source: SourceEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            reload()
        }
    }

    private fun reload() {
        viewModelScope.launch(Dispatchers.IO) {
            _sources.value = repo.getAllSources()
        }
    }

    private fun reporter() = object : ProgressReporter {
        override fun onStart() { _progress.value = 0 }
        override fun onProgress(percent: Int) { _progress.value = percent.coerceIn(0, 100) }
        override fun onStop() { _progress.value = null }
    }

    fun stopAllTasks() {
        viewModelScope.coroutineContext.cancelChildren()
        _progress.value = null
    }
}
