package ru.aloyaloya.here

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.design_system.theme.ThemeMode
import ru.aloyaloya.here.ui.HereApp

/**
 * Главная Activity приложения.
 *
 * Подписывается на состояние темы из [MainViewModel] и передает его
 * в [HereTheme], чтобы переключение `light`/`dark` применялось ко всему UI.
 */
class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appComponent = (application as HereApplication).appComponent

        viewModel = appComponent.viewModelFactory.create(MainViewModel::class.java)

        enableEdgeToEdge()
        setContent {
            val darkTheme by viewModel.darkTheme.collectAsState()

            HereTheme(themeMode = if (darkTheme) ThemeMode.DARK else ThemeMode.LIGHT) {
                HereApp(
                    darkTheme = darkTheme,
                    onThemeChange = viewModel::onThemeChange
                )
            }
        }
    }
}