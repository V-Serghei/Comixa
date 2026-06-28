package com.comixa.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comixa.core.domain.model.Shelf
import com.comixa.core.domain.repository.ShelfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShelvesUiState(
    val shelves: List<Shelf> = emptyList(),
    val showCreateDialog: Boolean = false,
    val renameTarget: Shelf? = null,
)

@HiltViewModel
class ShelvesViewModel @Inject constructor(
    private val shelfRepository: ShelfRepository,
) : ViewModel() {

    private val _dialogState = MutableStateFlow<DialogState>(DialogState.None)

    val uiState: StateFlow<ShelvesUiState> = combine(
        shelfRepository.getAll(),
        _dialogState,
    ) { shelves, dialog ->
        ShelvesUiState(
            shelves = shelves,
            showCreateDialog = dialog is DialogState.Create,
            renameTarget = (dialog as? DialogState.Rename)?.shelf,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ShelvesUiState(),
    )

    fun showCreateDialog() = _dialogState.update { DialogState.Create }
    fun showRenameDialog(shelf: Shelf) = _dialogState.update { DialogState.Rename(shelf) }
    fun dismissDialog() = _dialogState.update { DialogState.None }

    fun createShelf(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { shelfRepository.create(name.trim()) }
        _dialogState.update { DialogState.None }
    }

    fun renameShelf(shelfId: Long, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { shelfRepository.rename(shelfId, name.trim()) }
        _dialogState.update { DialogState.None }
    }

    fun deleteShelf(shelfId: Long) {
        viewModelScope.launch { shelfRepository.delete(shelfId) }
    }

    private sealed interface DialogState {
        data object None : DialogState
        data object Create : DialogState
        data class Rename(val shelf: Shelf) : DialogState
    }
}
