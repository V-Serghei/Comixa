package com.comixa.app.model

sealed class UiEvent {
    data class ShowToast(val msg: String) : UiEvent()
    data class OpenUrl(val url: String) : UiEvent()
    data class OpenCamera(val useFront: Boolean) : UiEvent()
    data class OpenPreview(val uri: String) : UiEvent()
}