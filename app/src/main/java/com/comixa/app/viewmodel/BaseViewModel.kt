package com.comixa.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel(private val sectionName: String) : ViewModel() {
    
    private val _text = MutableLiveData<String>().apply {
        value = "This is $sectionName"
    }
    val text: LiveData<String> = _text
}
