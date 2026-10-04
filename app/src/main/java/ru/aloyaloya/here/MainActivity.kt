package ru.aloyaloya.here

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.design_system.theme.ThemeMode
import ru.aloyaloya.here.ui.HereApp

/**
 * Главная Activity приложения.
 *
 * Подписывается на режим темы из [MainViewModel] и передает его в [HereTheme],
 * чтобы переключение применялось ко всему UI. Разрешенное значение темы нужно
 * и самой Activity: по нему подбирается вид системных баров.
 */
class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appComponent = (application as HereApplication).appComponent

        viewModel = appComponent.viewModelFactory.create(MainViewModel::class.java)

        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val darkTheme = when (themeMode) {
                ThemeMode.AUTO -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            LaunchedEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        lightScrim = Color.TRANSPARENT,
                        darkScrim = Color.TRANSPARENT,
                        detectDarkMode = { darkTheme }
                    ),
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim = NAVIGATION_BAR_LIGHT_SCRIM,
                        darkScrim = NAVIGATION_BAR_DARK_SCRIM,
                        detectDarkMode = { darkTheme }
                    )
                )
            }

            HereTheme(themeMode = themeMode) {
                HereApp(darkTheme = darkTheme)
            }
        }
    }

    private companion object {
        val NAVIGATION_BAR_LIGHT_SCRIM = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
        val NAVIGATION_BAR_DARK_SCRIM = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
    }
}