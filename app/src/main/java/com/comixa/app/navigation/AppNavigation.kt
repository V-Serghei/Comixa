package com.comixa.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.comixa.feature.library.LibraryScreen
import com.comixa.feature.reader.ReaderScreen
import com.comixa.feature.settings.SettingsScreen

private const val ROUTE_LIBRARY  = "library"
private const val ROUTE_READER   = "reader/{bookId}"
private const val ROUTE_SETTINGS = "settings"

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_LIBRARY,
        modifier = modifier,
    ) {
        composable(ROUTE_LIBRARY) {
            LibraryScreen(
                onBookClick = { book ->
                    navController.navigate("reader/${book.id}")
                }
            )
        }

        composable(ROUTE_READER) { backStack ->
            val bookId = backStack.arguments?.getString("bookId")?.toLongOrNull() ?: return@composable
            ReaderScreen(
                bookId = bookId,
                onBack = { navController.popBackStack() },
            )
        }

        composable(ROUTE_SETTINGS) {
            SettingsScreen()
        }
    }
}
