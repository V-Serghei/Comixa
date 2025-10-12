package com.comixa.app.viewmodel.lab1

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.comixa.app.adapter.lab1.LabNotificationWorker
import com.comixa.app.model.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class Lab1ViewModel(app: Application) : AndroidViewModel(app) {


    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onNotifyClicked() {
        // Планирование уведомления здесь (IO/бизнес в VM)
        val req = OneTimeWorkRequestBuilder<LabNotificationWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .setInputData(workDataOf("title" to "Comixa", "text" to "Hello! This is a notification in 10 seconds."))
            .build()
        WorkManager.getInstance(getApplication()).enqueue(req)
        _events.trySend(UiEvent.ShowToast("Notification scheduled in 10 seconds"))
    }

    fun onSearchClicked(rawQuery: String) {
        val q = rawQuery.trim()
        if (q.isEmpty()) {
            _events.trySend(UiEvent.ShowToast("Enter a query"))
            return
        }
        val url = "https://www.google.com/search?q=" + URLEncoder.encode(q, "UTF-8")
        _events.trySend(UiEvent.OpenUrl(url))
    }

    fun onOpenCameraClicked(useFront: Boolean) {
        _events.trySend(UiEvent.OpenCamera(useFront))
    }

    fun onOpenPreviewClicked() {
        val sp = getApplication<Application>().getSharedPreferences("lab1", 0)
        val last = sp.getString("last_photo_uri", null)
        if (last == null) {
            _events.trySend(UiEvent.ShowToast("No photos yet"))
        } else {
            _events.trySend(UiEvent.OpenPreview(last))
        }
    }
}