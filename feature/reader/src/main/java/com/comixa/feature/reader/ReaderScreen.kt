package com.comixa.feature.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.comixa.core.domain.model.ReadingDirection
import com.comixa.core.domain.model.ReadingStatus

@Composable
fun ReaderScreen(
    bookId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReaderViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val direction by viewModel.readingDirection.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            state.error != null -> {
                Text(
                    text = state.error!!,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            state.book != null && state.pageCount > 0 -> {
                if (direction == ReadingDirection.TOP_TO_BOTTOM) {
                    VerticalPageViewer(
                        state = state,
                        onPageVisible = viewModel::onPageSettled,
                        onMarkCompleted = viewModel::markAsCompleted,
                        onMarkUnread = viewModel::markAsUnread,
                        onAddBookmark = viewModel::addBookmarkAtPage,
                        onBack = onBack,
                    )
                } else {
                    HorizontalPageViewer(
                        state = state,
                        direction = direction,
                        onPageSettled = viewModel::onPageSettled,
                        onMarkCompleted = viewModel::markAsCompleted,
                        onMarkUnread = viewModel::markAsUnread,
                        onAddBookmark = viewModel::addBookmarkAtPage,
                        onBack = onBack,
                    )
                }
            }
            !state.isLoading -> {
                Text(
                    text = "No pages found",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HorizontalPageViewer(
    state: ReaderUiState,
    direction: ReadingDirection,
    onPageSettled: (Int) -> Unit,
    onMarkCompleted: () -> Unit,
    onMarkUnread: () -> Unit,
    onAddBookmark: (Int) -> Unit,
    onBack: () -> Unit,
) {
    val book = state.book!!
    val pagerState = rememberPagerState(
        initialPage = state.currentPage,
        pageCount = { state.pageCount },
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { onPageSettled(it) }
    }

    var overlayVisible by remember { mutableStateOf(false) }
    var isCurrentPageZoomed by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) { isCurrentPageZoomed = false }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            reverseLayout = direction == ReadingDirection.RIGHT_TO_LEFT,
            userScrollEnabled = !isCurrentPageZoomed,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            ZoomablePage(
                key = ComicPageKey(book.filePath, page, book.format),
                onTap = { overlayVisible = !overlayVisible },
                onZoomChanged = { zoomed ->
                    if (page == pagerState.currentPage) isCurrentPageZoomed = zoomed
                },
            )
        }

        AnimatedVisibility(
            visible = overlayVisible,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = book.title,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Text(
                        text = "${pagerState.currentPage + 1} / ${state.pageCount}",
                        modifier = Modifier.padding(end = 16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.6f),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
            )
        }
    }

    if (overlayVisible) {
        ReaderActionsSheet(
            currentPage = pagerState.currentPage,
            totalPages = state.pageCount,
            readingStatus = state.readingStatus,
            onMarkCompleted = {
                onMarkCompleted()
                overlayVisible = false
            },
            onMarkUnread = {
                onMarkUnread()
                overlayVisible = false
            },
            onAddBookmark = { onAddBookmark(pagerState.currentPage) },
            onDismiss = { overlayVisible = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerticalPageViewer(
    state: ReaderUiState,
    onPageVisible: (Int) -> Unit,
    onMarkCompleted: () -> Unit,
    onMarkUnread: () -> Unit,
    onAddBookmark: (Int) -> Unit,
    onBack: () -> Unit,
) {
    val book = state.book!!
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = state.currentPage)
    var overlayVisible by remember { mutableStateOf(false) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.collect { onPageVisible(it) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { overlayVisible = !overlayVisible }
                },
        ) {
            items(state.pageCount) { page ->
                AsyncImage(
                    model = ComicPageKey(book.filePath, page, book.format),
                    contentDescription = "Page ${page + 1}",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        AnimatedVisibility(
            visible = overlayVisible,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = book.title,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Text(
                        text = "${listState.firstVisibleItemIndex + 1} / ${state.pageCount}",
                        modifier = Modifier.padding(end = 16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.6f),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
            )
        }
    }

    if (overlayVisible) {
        ReaderActionsSheet(
            currentPage = listState.firstVisibleItemIndex,
            totalPages = state.pageCount,
            readingStatus = state.readingStatus,
            onMarkCompleted = {
                onMarkCompleted()
                overlayVisible = false
            },
            onMarkUnread = {
                onMarkUnread()
                overlayVisible = false
            },
            onAddBookmark = { onAddBookmark(listState.firstVisibleItemIndex) },
            onDismiss = { overlayVisible = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderActionsSheet(
    currentPage: Int,
    totalPages: Int,
    readingStatus: ReadingStatus,
    onMarkCompleted: () -> Unit,
    onMarkUnread: () -> Unit,
    onAddBookmark: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartialExpansion = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Black.copy(alpha = 0.85f),
        contentColor = Color.White,
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(
                text = "Page ${currentPage + 1} of $totalPages",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
            Text(
                text = when (readingStatus) {
                    ReadingStatus.UNREAD -> "Unread"
                    ReadingStatus.IN_PROGRESS -> "In progress"
                    ReadingStatus.COMPLETED -> "Completed"
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
            )
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (readingStatus != ReadingStatus.COMPLETED) {
                    ActionButton(
                        icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(22.dp)) },
                        label = "Mark as read",
                        onClick = onMarkCompleted,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (readingStatus != ReadingStatus.UNREAD) {
                    ActionButton(
                        icon = { Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, modifier = Modifier.size(22.dp)) },
                        label = "Mark as unread",
                        onClick = onMarkUnread,
                        modifier = Modifier.weight(1f),
                    )
                }
                ActionButton(
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(22.dp)) },
                    label = "Bookmark",
                    onClick = {
                        onAddBookmark()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ActionButton(
    icon: @Composable () -> Unit,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(onClick = onClick, modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            icon()
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White)
        }
    }
}

@Composable
private fun ZoomablePage(
    key: ComicPageKey,
    onTap: () -> Unit,
    onZoomChanged: (Boolean) -> Unit = {},
) {
    var scale by remember(key.pageIndex) { mutableFloatStateOf(1f) }
    var offset by remember(key.pageIndex) { mutableStateOf(Offset.Zero) }

    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(1f, 6f)
        scale = newScale
        offset = if (newScale > 1f) offset + panChange else Offset.Zero
        onZoomChanged(newScale > 1f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // transformable first: inner modifier gets multi-touch events before detectTapGestures
            .transformable(state = transformState)
            .pointerInput(key.pageIndex) {
                detectTapGestures(
                    onTap = { onTap() },
                    onDoubleTap = {
                        scale = 1f
                        offset = Offset.Zero
                        onZoomChanged(false)
                    },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = key,
            contentDescription = "Page ${key.pageIndex + 1}",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                },
        )
    }
}
