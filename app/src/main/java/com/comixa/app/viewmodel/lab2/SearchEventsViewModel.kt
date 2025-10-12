package com.comixa.app.viewmodel.lab2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.comixa.data.event.Event
import com.comixa.data.event.EventXmlStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchEventsViewModel(app: Application) : AndroidViewModel(app) {
    private val store = EventXmlStore(app)

    private val _all = MutableStateFlow<List<Event>>(emptyList())
    val all: StateFlow<List<Event>> = _all

    fun reload() {
        viewModelScope.launch(Dispatchers.IO) {
            _all.value = store.getAll()
        }
    }

    fun delete(id: Long, onDone: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            store.delete(id)
            _all.value = store.getAll()
            onDone()
        }
    }
}