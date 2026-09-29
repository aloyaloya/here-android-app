package ru.aloyaloya.here.ui

import androidx.annotation.StringRes

/**
 * Режим приложения, который подменяет панели раздела своей шапкой.
 *
 * @param titleResId Название режима в шапке.
 * @param onExit Колбэк выхода: и по стрелке в шапке, и по системному «назад».
 */
data class HereContextMode(
    @StringRes val titleResId: Int,
    val onExit: () -> Unit
)
