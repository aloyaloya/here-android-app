package ru.aloyaloya.here

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.here.navigation.OnboardingNavHost
import ru.aloyaloya.here.ui.HereApp

/**
 * Главная Activity приложения.
 *
 * Тему берет из конфигурации: выбор в настройках применяется через `AppCompatDelegate`.
 * По ней же подбирается вид системных баров.
 * Пока онбординг не пройден, вместо приложения показывается он.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appComponent = (application as HereApplication).appComponent

        viewModel = appComponent.viewModelFactory.create(MainViewModel::class.java)

        enableEdgeToEdge()
        setContent {
            val onboardingCompleted by viewModel.onboardingCompleted.collectAsState()
            val darkTheme = isSystemInDarkTheme()

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

            HereTheme(darkTheme = darkTheme) {
                Crossfade(targetState = onboardingCompleted, label = "onboarding") { completed ->
                    if (completed) {
                        HereApp(darkTheme = darkTheme)
                    } else {
                        OnboardingNavHost(onFinished = viewModel::completeOnboarding)
                    }
                }
            }
        }
    }

    private companion object {
        val NAVIGATION_BAR_LIGHT_SCRIM = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
        val NAVIGATION_BAR_DARK_SCRIM = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
    }
}