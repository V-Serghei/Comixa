package com.comixa.app.model

sealed class LoadingEvent {
    data object Start : LoadingEvent()
    data class Progress(val percent: Int) : LoadingEvent()
    data object Stop : LoadingEvent()
}
