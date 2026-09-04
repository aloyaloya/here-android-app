package ru.aloyaloya.memory.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.memory.model.MemoryUiState

/**
 * Экран воспоминания.
 *
 * @param uiState Состояние экрана.
 * @param onBackClick Колбэк возврата назад.
 * @param modifier [Modifier], применяемый к экрану.
 */
@Composable
fun MemoryScreen(
    uiState: MemoryUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(HereTheme.colors.background)
    ) {
        when (uiState) {
            MemoryUiState.Loading -> CircularProgressIndicator(color = HereTheme.colors.accent)

            is MemoryUiState.Content -> Text(
                text = uiState.memory.title,
                style = MaterialTheme.typography.headlineSmall,
                color = HereTheme.colors.textPrimary
            )

            MemoryUiState.NotFound -> LaunchedEffect(Unit) { onBackClick() }
        }
    }
}
