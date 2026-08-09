package ru.aloyaloya.design_system.component.button

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import ru.aloyaloya.design_system.extension.buttonShadow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Основная кнопка приложения Here — широкая пилюля акцентного цвета.
 *
 * Пока кнопка неактивна, она гасится до приглушенного фона и остается без тени.
 *
 * @param text Подпись кнопки.
 * @param onClick Колбэк нажатия.
 * @param modifier [Modifier], применяемый к кнопке.
 * @param enabled Активна ли кнопка.
 * @param height Высота кнопки: в листах она ниже, чем на экране.
 */
@Composable
fun HerePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = HereSize.PrimaryButton.height
) {
    val colors = HereTheme.colors

    val background by animateColorAsState(
        targetValue = if (enabled) colors.accent else colors.surfaceMuted,
        label = "primary-button-background"
    )

    val contentColor by animateColorAsState(
        targetValue = if (enabled) colors.onAccent else colors.textTertiary,
        label = "primary-button-content"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .then(if (enabled) Modifier.buttonShadow(HereShape.pill) else Modifier)
            .clip(HereShape.pill)
            .background(background)
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor
        )
    }
}