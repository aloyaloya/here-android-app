package ru.aloyaloya.here

import android.graphics.Color
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import ru.aloyaloya.design_system.component.celebration.Confetti
import ru.aloyaloya.design_system.theme.HereMotion
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.here.navigation.OnboardingNavHost
import ru.aloyaloya.here.ui.HereApp

/**
 * Главная Activity приложения.
 *
 * При запуске показывает системный сплеш: знак вырастает, а когда приложение готово,
 * сжимается, и сплеш растворяется.
 * Тему берет из конфигурации: выбор в настройках применяет `SettingsRepository`.
 * По ней же подбирается вид системных баров.
 * Пока онбординг не пройден, вместо приложения показывается он.
 * Выданный в онбординге доступ отмечается конфетти поверх перехода в приложение.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        installAnimatedSplashScreen()
        super.onCreate(savedInstanceState)

        val appComponent = (application as HereApplication).appComponent

        viewModel = appComponent.viewModelFactory.create(MainViewModel::class.java)

        applySystemBars()
        setContent {
            val onboardingCompleted by viewModel.onboardingCompleted.collectAsState()
            val hapticsEnabled by viewModel.hapticsEnabled.collectAsState()
            val darkTheme = isSystemInDarkTheme()
            var celebrating by remember { mutableStateOf(false) }

            HereTheme(darkTheme = darkTheme) {
                val haptic = LocalHapticFeedback.current

                CompositionLocalProvider(
                    LocalHapticFeedback provides if (hapticsEnabled) haptic else NoHapticFeedback
                ) {
                    Box {
                        Crossfade(
                            targetState = onboardingCompleted,
                            label = "onboarding"
                        ) { completed ->
                            if (completed) {
                                HereApp(darkTheme = darkTheme)
                            } else {
                                OnboardingNavHost(
                                    onFinished = viewModel::completeOnboarding,
                                    onPermissionGranted = {
                                        celebrating = true
                                        viewModel.completeOnboarding()
                                    }
                                )
                            }
                        }

                        if (celebrating) {
                            Confetti(onEnded = { celebrating = false })
                        }
                    }
                }
            }
        }
    }

    /** Показывает системный сплеш */
    private fun installAnimatedSplashScreen() {
        installSplashScreen().setOnExitAnimationListener { splash ->
            val delayMillis = (splash.iconAnimationStartMillis +
                    splash.iconAnimationDurationMillis -
                    System.currentTimeMillis()).coerceAtLeast(0L)
            val durationMillis = HereMotion.Duration.medium.toLong()

            splash.iconView.animate()
                .scaleX(0f)
                .scaleY(0f)
                .setInterpolator(AccelerateInterpolator())
                .setStartDelay(delayMillis)
                .setDuration(durationMillis)
            splash.view.animate()
                .alpha(0f)
                .setStartDelay(delayMillis)
                .setDuration(durationMillis)
                .withEndAction {
                    splash.remove()
                    applySystemBars()
                }
        }
    }

    /** Рисует приложение под системными барами и подбирает их вид по теме из конфигурации. */
    private fun applySystemBars() {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = NAVIGATION_BAR_LIGHT_SCRIM,
                darkScrim = NAVIGATION_BAR_DARK_SCRIM
            )
        )
    }

    /** Отклик, который ничего не делает: тактильный отклик выключен в настройках. */
    private object NoHapticFeedback : HapticFeedback {
        override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) = Unit
    }

    private companion object {
        val NAVIGATION_BAR_LIGHT_SCRIM = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
        val NAVIGATION_BAR_DARK_SCRIM = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
    }
}