package com.comixa.app.ui.labs.lab2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.comixa.data.event.Event
import com.comixa.data.event.EventXmlStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class OrganizerMainViewModel(app: Application) : AndroidViewModel(app) {

    private val store = EventXmlStore(app)

    private val _selectedDayStart = MutableStateFlow(dayStart(System.currentTimeMillis()))
    val selectedDayStart: StateFlow<Long> = _selectedDayStart.asStateFlow()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    fun setDay(y: Int, m: Int, d: Int) {
        val cal = Calendar.getInstance()
        cal.set(y, m, d, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        _selectedDayStart.value = cal.timeInMillis
        reload()
    }

    fun reload() {
        viewModelScope.launch(Dispatchers.IO) {
            val all = store.getAll()
            val start = _selectedDayStart.value
            val end = start + 24L * 60 * 60 * 1000
            val day = all.filter { it.timeMillis in start until end }.sortedBy { it.timeMillis }
            _events.value = day
        }
    }

    fun delete(id: Long, onDone: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            store.delete(id)
            reload()
            onDone()
        }
    }

    private fun dayStart(millis: Long): Long = Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
