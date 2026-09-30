package ru.aloyaloya.summary.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme
import ru.aloyaloya.summary.model.SummaryPeriod
import ru.aloyaloya.summary.model.SummaryUiState

/**
 * Экран итогов: что и где чувствовалось за выбранный период.
 *
 * @param uiState Состояние экрана.
 * @param onPeriodSelected Колбэк выбора периода.
 * @param modifier Внешний [Modifier] экрана.
 */
@Composable
fun SummaryScreen(
    uiState: SummaryUiState,
    onPeriodSelected: (SummaryPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HereTheme.colors.background)
    ) {
        when (uiState) {
            SummaryUiState.Loading -> {
                CircularProgressIndicator(
                    color = HereTheme.colors.accent,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is SummaryUiState.Content -> SummaryContent(
                uiState = uiState,
                onPeriodSelected = onPeriodSelected
            )
        }
    }
}

@Composable
private fun SummaryContent(
    uiState: SummaryUiState.Content,
    onPeriodSelected: (SummaryPeriod) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(HereSpacing.xl),
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(
                top = HereSize.TopAppBar.height,
                bottom = HereSize.NavBar.height
            )
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = HereSpacing.l,
                vertical = HereSpacing.s
            )
    ) {
        PeriodSelector(
            selected = uiState.period,
            onSelect = onPeriodSelected
        )

        // TODO: карточка периода, смесь эмоций, места настроения и «Вспомнить»
    }
}

@Composable
private fun PeriodSelector(
    selected: SummaryPeriod,
    onSelect: (SummaryPeriod) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(HereSpacing.s)) {
        SummaryPeriod.entries.forEach { period ->
            PeriodChip(
                text = stringResource(period.labelResId),
                selected = period == selected,
                onClick = { onSelect(period) }
            )
        }
    }
}

@Composable
private fun PeriodChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = HereTheme.colors

    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = if (selected) colors.accent else colors.textSecondary,
        modifier = Modifier
            .clip(HereShape.pill)
            .background(if (selected) colors.accentContainer else colors.surfaceMuted)
            .clickable(onClick = onClick)
            .padding(horizontal = HereSpacing.l, vertical = HereSpacing.s)
    )
}
