package com.comixa.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.comixa.app.navigation.AppNavigation
import com.comixa.core.ui.theme.ComixaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDark by remember { mutableStateOf(true) }
            ComixaTheme(darkTheme = isDark) {
                AppNavigation(
                    isDarkTheme = isDark,
                    onThemeToggle = { isDark = !isDark },
                )
            }
        }
    }
}
