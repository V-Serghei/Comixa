package com.comixa.app.viewmodel.lab2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.comixa.data.event.Event
import com.comixa.data.event.EventXmlStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

class AddViewModel(app: Application) : AndroidViewModel(app) {
    private val store = EventXmlStore(app)
    val cal: Calendar = Calendar.getInstance()

    fun initFromDayStart(dayStart: Long?) {
        if (dayStart != null && dayStart > 0) cal.timeInMillis = dayStart
    }

    fun setDate(y: Int, m: Int, d: Int) { cal.set(y, m, d) }
    fun setTime(h: Int, min: Int) {
        cal.set(Calendar.HOUR_OF_DAY, h)
        cal.set(Calendar.MINUTE, min)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
    }

    fun save(info: String, onDone: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val e = Event(UUID.randomUUID().toString(), cal.timeInMillis, info)
            store.add(e)
            onDone()
        }
    }
}