package com.comixa.feature.library

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.comixa.core.domain.model.ComicBook
import com.comixa.core.domain.model.ComicFormat
import com.comixa.core.domain.model.ComicPageKey
import com.comixa.core.domain.model.ReadingStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onBookClick: (ComicBook) -> Unit,
    onSeriesClick: (String) -> Unit,
    openDrawer: () -> Unit,
    onBrowseFolders: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var fabExpanded by remember { mutableStateOf(false) }
    var searchActive by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val folderPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
    ) { uri -> uri?.let { viewModel.onFolderSelected(it) } }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
            onBrowseFolders()
        }
    }

    BackHandler(enabled = fabExpanded || searchActive) {
        if (searchActive) {
            searchActive = false
            viewModel.setSearchQuery("")
            focusManager.clearFocus()
        } else {
            fabExpanded = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (searchActive) {
                        TextField(
                            value = state.filter.searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Search comics, series…") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        )
                    } else {
                        Text("Library")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Open menu")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        searchActive = !searchActive
                        if (!searchActive) {
                            viewModel.setSearchQuery("")
                            focusManager.clearFocus()
                        }
                    }) {
                        Icon(
                            imageVector = if (searchActive) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = if (searchActive) "Close search" else "Search",
                        )
                    }
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                        ) {
                            SortOrder.entries.forEach { order ->
                                DropdownMenuItem(
                                    text = { Text(order.label) },
                                    onClick = {
                                        viewModel.setSortOrder(order)
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (state.filter.sortOrder == order) {
                                            Icon(Icons.Default.Check, contentDescription = null)
                                        }
                                    },
                                )
                            }
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            SpeedDialFab(
                expanded = fabExpanded,
                onToggle = { fabExpanded = !fabExpanded },
                onBrowseFolders = {
                    fabExpanded = false
                    val hasPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                        Environment.isExternalStorageManager()
                    if (hasPermission) {
                        onBrowseFolders()
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        permissionLauncher.launch(
                            Intent(
                                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                Uri.parse("package:${context.packageName}"),
                            )
                        )
                    } else {
                        folderPicker.launch(null)
                    }
                },
                onPickFolder = {
                    fabExpanded = false
                    folderPicker.launch(null)
                },
            )
        },
        modifier = modifier,
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (state.isScanning) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ReadingStatus.entries.forEach { status ->
                    FilterChip(
                        selected = state.filter.statusFilter == status,
                        onClick = {
                            viewModel.setStatusFilter(
                                if (state.filter.statusFilter == status) null else status,
                            )
                        },
                        label = { Text(status.label) },
                    )
                }
                ComicFormat.entries.forEach { format ->
                    FilterChip(
                        selected = state.filter.formatFilter == format,
                        onClick = {
                            viewModel.setFormatFilter(
                                if (state.filter.formatFilter == format) null else format,
                            )
                        },
                        label = { Text(format.name) },
                    )
                }
            }

            when {
                state.items.isEmpty() && !state.isScanning && state.filter.searchQuery.isBlank()
                    && state.filter.formatFilter == null && state.filter.statusFilter == null -> {
                    EmptyLibrary()
                }
                state.items.isEmpty() && !state.isScanning -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No results",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(
                            items = state.items,
                            key = { item ->
                                when (item) {
                                    is LibraryItem.Single -> "book_${item.item.book.id}"
                                    is LibraryItem.Series -> "series_${item.name}"
                                }
                            },
                        ) { item ->
                            when (item) {
                                is LibraryItem.Single -> BookCard(
                                    item = item.item,
                                    onClick = { onBookClick(item.item.book) },
                                )
                                is LibraryItem.Series -> SeriesCard(
                                    series = item,
                                    onClick = { onSeriesClick(item.name) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyLibrary() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.AutoStories,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Your library is empty",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Tap + to add folders with your comics.\nSupports CBZ, PDF and CBR files.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
internal fun BookCard(item: BookWithProgress, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Column {
            AsyncImage(
                model = ComicPageKey(filePath = item.book.filePath, pageIndex = 0, format = item.book.format),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f),
            )
            val progress = item.progress
            if (progress != null && progress.totalPages > 0) {
                LinearProgressIndicator(
                    progress = { (progress.currentPage + 1).toFloat() / progress.totalPages },
                    modifier = Modifier.fillMaxWidth().height(3.dp),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = item.book.title,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.book.format.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
internal fun SeriesCard(series: LibraryItem.Series, onClick: () -> Unit) {
    val cover = series.cover
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Column {
            BadgedBox(
                badge = {
                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                        Text(series.books.size.toString(), style = MaterialTheme.typography.labelSmall)
                    }
                },
                modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f),
            ) {
                AsyncImage(
                    model = ComicPageKey(
                        filePath = cover.book.filePath,
                        pageIndex = 0,
                        format = cover.book.format,
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            if (series.readCount > 0) {
                LinearProgressIndicator(
                    progress = { series.readCount.toFloat() / series.books.size },
                    modifier = Modifier.fillMaxWidth().height(3.dp),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = series.name,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${series.books.size} issues",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun SpeedDialFab(
    expanded: Boolean,
    onToggle: () -> Unit,
    onBrowseFolders: () -> Unit,
    onPickFolder: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.End) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
        ) {
            Column(horizontalAlignment = Alignment.End) {
                SpeedDialItem(
                    label = "Browse folders",
                    icon = { Icon(Icons.Default.FolderOpen, contentDescription = null) },
                    onClick = onBrowseFolders,
                )
                SpeedDialItem(
                    label = "Quick pick (SAF)",
                    icon = { Icon(Icons.Default.Folder, contentDescription = null) },
                    onClick = onPickFolder,
                )
            }
        }
        FloatingActionButton(onClick = onToggle) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = if (expanded) "Collapse" else "Add folder",
            )
        }
    }
}

@Composable
private fun SpeedDialItem(label: String, icon: @Composable () -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier.padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 4.dp,
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Spacer(Modifier.width(12.dp))
        SmallFloatingActionButton(onClick = onClick) { icon() }
    }
}

private val SortOrder.label: String
    get() = when (this) {
        SortOrder.TITLE_ASC -> "Title A → Z"
        SortOrder.TITLE_DESC -> "Title Z → A"
        SortOrder.RECENTLY_ADDED -> "Recently added"
        SortOrder.RECENTLY_READ -> "Recently read"
    }

private val ReadingStatus.label: String
    get() = when (this) {
        ReadingStatus.UNREAD -> "Unread"
        ReadingStatus.IN_PROGRESS -> "In progress"
        ReadingStatus.COMPLETED -> "Completed"
    }
