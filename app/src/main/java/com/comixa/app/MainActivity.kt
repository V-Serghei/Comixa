package com.comixa.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.comixa.app.navigation.AppNavigation
import com.comixa.core.ui.theme.ComixaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDark by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            ComixaTheme(darkTheme = isDark) {
                AppNavigation(
                    isDarkTheme = isDark,
                    onThemeToggle = { viewModel.toggleTheme() },
                )
            }
        }
    }
}
