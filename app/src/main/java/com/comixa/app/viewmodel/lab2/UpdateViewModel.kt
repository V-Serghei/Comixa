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
import kotlinx.coroutines.withContext
import java.util.Calendar

class UpdateViewModel(app: Application) : AndroidViewModel(app) {
    private val store = EventXmlStore(app)

    private val _model = MutableStateFlow<Event?>(null)
    val model: StateFlow<Event?> = _model

    val cal: Calendar = Calendar.getInstance()

    fun load(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val e = store.getAll().firstOrNull { it.id == id }
            _model.value = e
            e?.let { cal.timeInMillis = it.timeMillis }
        }
    }

    fun setDate(y: Int, m: Int, d: Int) { cal.set(y, m, d) }
    fun setTime(h: Int, min: Int) {
        cal.set(Calendar.HOUR_OF_DAY, h)
        cal.set(Calendar.MINUTE, min)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
    }

    fun save(newDesc: String, onDone: () -> Unit) {
        val e = _model.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            e.timeMillis = cal.timeInMillis
            e.description = newDesc
            store.update(e)
            withContext(Dispatchers.Main) { onDone() } // колбэк на UI-поток
        }
    }
}
