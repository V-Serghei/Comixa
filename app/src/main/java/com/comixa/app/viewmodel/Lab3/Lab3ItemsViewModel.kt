package com.comixa.app.viewmodel.Lab3

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.comixa.data.rss.ArticleEntity
import com.comixa.data.rss.RssRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class Lab3ItemsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = RssRepository(app)
    private val sourceId = MutableStateFlow<Long?>(null)

    val articles: StateFlow<List<ArticleEntity>> =
        sourceId.filterNotNull()
            .flatMapLatest { repo.observeArticles(it) }
            .stateIn(viewModelScope, SharingStarted.Companion.Lazily, emptyList())

    fun setSource(id: Long) { sourceId.value = id }

    fun refresh() = viewModelScope.launch(Dispatchers.IO) {
        sourceId.value?.let { repo.refreshSource(it) }
    }
}