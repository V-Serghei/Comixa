package com.comixa.feature.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.comixa.core.domain.model.ReadingDirection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    openDrawer: () -> Unit = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val prefs by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Open menu")
                    }
                },
            )
        },
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setDarkTheme(!prefs.isDarkTheme) }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Dark theme",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = prefs.isDarkTheme,
                    onCheckedChange = { viewModel.setDarkTheme(it) },
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Reading direction",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(4.dp))

            ReadingDirection.entries.forEach { direction ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setReadingDirection(direction) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = prefs.readingDirection == direction,
                        onClick = { viewModel.setReadingDirection(direction) },
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = direction.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            text = direction.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

private val ReadingDirection.displayName: String
    get() = when (this) {
        ReadingDirection.LEFT_TO_RIGHT -> "Left to right"
        ReadingDirection.RIGHT_TO_LEFT -> "Right to left"
        ReadingDirection.TOP_TO_BOTTOM -> "Top to bottom"
    }

private val ReadingDirection.description: String
    get() = when (this) {
        ReadingDirection.LEFT_TO_RIGHT -> "Western comics"
        ReadingDirection.RIGHT_TO_LEFT -> "Manga"
        ReadingDirection.TOP_TO_BOTTOM -> "Webtoon / vertical scroll"
    }
