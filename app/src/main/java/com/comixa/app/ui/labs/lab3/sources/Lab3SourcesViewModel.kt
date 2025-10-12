package com.comixa.app.ui.labs.lab3.sources

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.comixa.data.rss.RssRepository
import com.comixa.data.rss.SourceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class Lab3SourcesViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = RssRepository(app)

    val sources = repo.observeSources()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun add(url: String) = viewModelScope.launch(Dispatchers.IO) {
        repo.addSourceAndSync(url.trim())
    }
    fun delete(source: SourceEntity) = viewModelScope.launch(Dispatchers.IO) {
        repo.deleteSource(source)
    }
    fun refresh(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        repo.refreshSource(id)
    }
}
