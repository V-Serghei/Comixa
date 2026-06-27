package com.comixa.feature.library

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.comixa.core.domain.model.ComicBook
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onBookClick: (ComicBook) -> Unit,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val folderPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
    ) { uri -> uri?.let { viewModel.onFolderSelected(it) } }

    // Launched after user returns from system Settings where they grant MANAGE_EXTERNAL_STORAGE
    val permissionSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
            viewModel.startScan()
        }
    }

    if (state.showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissPermissionDialog() },
            title = { Text("Access to all files") },
            text = {
                Text(
                    "Grant \"All files access\" so Comixa can scan any folder on your device, " +
                    "including Downloads and SD card. Otherwise, use the folder picker for limited access.",
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.dismissPermissionDialog()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        permissionSettingsLauncher.launch(
                            Intent(
                                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                "package:${context.packageName}".toUri(),
                            )
                        )
                    }
                }) { Text("Grant Access") }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.dismissPermissionDialog()
                    folderPicker.launch(null)
                }) { Text("Use Folder Picker") }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Library") },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Open menu")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                    !Environment.isExternalStorageManager()
                if (needsPermission) {
                    viewModel.showPermissionDialog()
                } else {
                    folderPicker.launch(null)
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add folder")
            }
        },
        modifier = modifier,
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (state.isScanning) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (state.books.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (state.isScanning) "Scanning…" else "Tap + to add a comics folder",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(items = state.books, key = { it.id }) { book ->
                        BookCard(book = book, onClick = { onBookClick(book) })
                    }
                }
            }
        }
    }
}

@Composable
private fun BookCard(
    book: ComicBook,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = book.format.name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
