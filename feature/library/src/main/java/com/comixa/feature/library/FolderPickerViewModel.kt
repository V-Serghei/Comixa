package com.comixa.feature.library

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comixa.core.domain.repository.WatchedFolderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class FolderEntry(
    val file: File,
    val isWatched: Boolean,
)

data class FolderPickerState(
    val currentPath: String = "",
    val entries: List<FolderEntry> = emptyList(),
    val watchedPaths: Set<String> = emptySet(),
)

@HiltViewModel
class FolderPickerViewModel @Inject constructor(
    private val watchedFolderRepository: WatchedFolderRepository,
) : ViewModel() {

    private val root = Environment.getExternalStorageDirectory()
    private val _currentDir = MutableStateFlow(root)
    private val _localWatched = MutableStateFlow<Set<String>>(emptySet())
    private var dbWatched: Set<String> = emptySet()

    init {
        viewModelScope.launch {
            dbWatched = watchedFolderRepository.getSnapshot().map { it.path }.toSet()
            _localWatched.value = dbWatched
        }
    }

    val state: StateFlow<FolderPickerState> = combine(_currentDir, _localWatched) { dir, watched ->
        val entries = (dir.listFiles() ?: emptyArray())
            .filter { it.isDirectory && !it.name.startsWith(".") && it.name != "Android" }
            .sortedBy { it.name.lowercase() }
            .map { FolderEntry(file = it, isWatched = it.absolutePath in watched) }
        FolderPickerState(
            currentPath = dir.absolutePath,
            entries = entries,
            watchedPaths = watched,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FolderPickerState(currentPath = root.absolutePath),
    )

    fun navigateTo(dir: File) {
        _currentDir.value = dir
    }

    fun navigateUp() {
        val parent = _currentDir.value.parentFile
        if (parent != null && _currentDir.value.absolutePath != root.absolutePath) {
            _currentDir.value = parent
        }
    }

    fun isAtRoot(): Boolean = _currentDir.value.absolutePath == root.absolutePath

    fun toggleWatch(path: String) {
        _localWatched.update { current ->
            if (path in current) current - path else current + path
        }
    }

    fun save() {
        viewModelScope.launch {
            val toAdd = _localWatched.value - dbWatched
            val toRemove = dbWatched - _localWatched.value
            toAdd.forEach { watchedFolderRepository.add(it) }
            toRemove.forEach { watchedFolderRepository.remove(it) }
            dbWatched = _localWatched.value
        }
    }
}
