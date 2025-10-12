package com.comixa.app.ui.labs.lab3.items

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.comixa.data.rss.ArticleEntity
import com.comixa.data.rss.RssRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class Lab3ItemsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = RssRepository(app)
    private val sourceId = MutableStateFlow<Long?>(null)

    val articles: StateFlow<List<ArticleEntity>> =
        sourceId.filterNotNull()
            .flatMapLatest { repo.observeArticles(it) }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setSource(id: Long) { sourceId.value = id }

    fun refresh() = viewModelScope.launch(Dispatchers.IO) {
        sourceId.value?.let { repo.refreshSource(it) }
    }
}
