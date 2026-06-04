package ru.aloyaloya.ui.theme

import androidx.compose.runtime.compositionLocalOf

/**
 * Тёмная тема приложения (`true` — тёмная, `false` — светлая).
 *
 * Задаётся в корне UI через `CompositionLocalProvider`; экраны и виджеты (например карта)
 * читают [current] без проброса параметров через навигацию.
 */
val LocalAppDarkTheme = compositionLocalOf { false }
