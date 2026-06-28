package com.comixa.app.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.comixa.feature.library.FolderPickerScreen
import com.comixa.feature.library.LibraryScreen
import com.comixa.feature.library.SeriesListScreen
import com.comixa.feature.library.SeriesScreen
import com.comixa.feature.library.ShelfDetailScreen
import com.comixa.feature.library.ShelvesScreen
import com.comixa.feature.reader.ReaderScreen
import com.comixa.feature.settings.SettingsScreen
import kotlinx.coroutines.launch

private const val ROUTE_LIBRARY       = "library"
private const val ROUTE_READER        = "reader/{bookId}"
private const val ROUTE_SETTINGS      = "settings"
private const val ROUTE_FOLDER_PICKER = "folder_picker"
private const val ROUTE_SERIES        = "series/{seriesName}"
private const val ROUTE_SERIES_LIST   = "series_list"
private const val ROUTE_SHELVES       = "shelves"
private const val ROUTE_SHELF_DETAIL  = "shelf/{shelfId}"

@Composable
fun AppNavigation(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    navController: NavHostController = rememberNavController(),
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    val openDrawer: () -> Unit = { scope.launch { drawerState.open() } }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentRoute != ROUTE_READER,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Comixa",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Library") },
                    selected = currentRoute == ROUTE_LIBRARY,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(ROUTE_LIBRARY) {
                            popUpTo(ROUTE_LIBRARY) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AutoStories, contentDescription = null) },
                    label = { Text("Series") },
                    selected = currentRoute == ROUTE_SERIES_LIST,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(ROUTE_SERIES_LIST) {
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Bookmarks, contentDescription = null) },
                    label = { Text("Shelves") },
                    selected = currentRoute == ROUTE_SHELVES,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(ROUTE_SHELVES) {
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                    selected = currentRoute == ROUTE_SETTINGS,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(ROUTE_SETTINGS) {
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )

                Spacer(Modifier.weight(1f))
                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text(if (isDarkTheme) "Dark theme" else "Light theme") },
                    selected = false,
                    onClick = onThemeToggle,
                    badge = {
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { onThemeToggle() },
                        )
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                )

                Spacer(Modifier.height(16.dp))
            }
        },
    ) {
        NavHost(
            navController = navController,
            startDestination = ROUTE_LIBRARY,
        ) {
            composable(ROUTE_LIBRARY) {
                LibraryScreen(
                    onBookClick = { book -> navController.navigate("reader/${book.id}") },
                    onSeriesClick = { name -> navController.navigate("series/${Uri.encode(name)}") },
                    openDrawer = openDrawer,
                    onBrowseFolders = { navController.navigate(ROUTE_FOLDER_PICKER) },
                )
            }

            composable(
                route = ROUTE_READER,
                arguments = listOf(navArgument("bookId") { type = NavType.LongType }),
            ) { backStack ->
                val bookId = backStack.arguments?.getLong("bookId") ?: return@composable
                ReaderScreen(
                    bookId = bookId,
                    onBack = { navController.popBackStack() },
                )
            }

            composable(ROUTE_SETTINGS) {
                SettingsScreen(openDrawer = openDrawer)
            }

            composable(ROUTE_SERIES_LIST) {
                SeriesListScreen(
                    onSeriesClick = { name -> navController.navigate("series/${Uri.encode(name)}") },
                    openDrawer = openDrawer,
                )
            }

            composable(ROUTE_SHELVES) {
                ShelvesScreen(
                    onShelfClick = { shelfId -> navController.navigate("shelf/$shelfId") },
                    openDrawer = openDrawer,
                )
            }

            composable(
                route = ROUTE_SHELF_DETAIL,
                arguments = listOf(navArgument("shelfId") { type = NavType.LongType }),
            ) {
                ShelfDetailScreen(
                    onBookClick = { book -> navController.navigate("reader/${book.id}") },
                    onBack = { navController.popBackStack() },
                )
            }

            composable(ROUTE_FOLDER_PICKER) {
                FolderPickerScreen(
                    onDone = { navController.popBackStack() },
                )
            }

            composable(
                route = ROUTE_SERIES,
                arguments = listOf(navArgument("seriesName") { type = NavType.StringType }),
            ) { backStack ->
                val seriesName = backStack.arguments?.getString("seriesName") ?: return@composable
                SeriesScreen(
                    onBookClick = { book -> navController.navigate("reader/${book.id}") },
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
