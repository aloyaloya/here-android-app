package ru.aloyaloya.calendar.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.aloyaloya.calendar.model.CalendarUiState
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Экран календаря: воспоминания по дням месяца.
 *
 * Верхнюю панель рисует приложение, экран отвечает только за контент под ней.
 *
 * @param uiState Состояние экрана.
 * @param modifier Внешний [Modifier] экрана.
 */
@Composable
fun CalendarScreen(
    uiState: CalendarUiState,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(HereTheme.colors.background)
    ) {
        when (uiState) {
            CalendarUiState.Loading -> {
                CircularProgressIndicator(color = HereTheme.colors.accent)
            }

            is CalendarUiState.Content -> Unit
        }
    }
}
