package ru.aloyaloya.design_system.component.location

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.aloyaloya.design_system.extension.overlayShadow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereSpacing
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Отметка пользователя на карте: точка в ореоле и подпись под ними.
 *
 * @param label Подпись: «Ты здесь».
 * @param modifier [Modifier], применяемый к отметке.
 */
@Composable
fun UserLocationMarker(
    label: String,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors
    val sizes = HereSize.UserLocation
    val haloAlpha = if (colors.isDark) sizes.darkHaloAlpha else sizes.haloAlpha

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HereSpacing.xs),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(sizes.haloSize)
                .background(color = colors.textPrimary.copy(alpha = haloAlpha), shape = CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .size(sizes.dotSize)
                    .overlayShadow(CircleShape)
                    .background(color = colors.textPrimary, shape = CircleShape)
                    .border(width = sizes.dotBorder, color = colors.surface, shape = CircleShape)
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textPrimary,
            modifier = Modifier
                .overlayShadow(HereShape.pill)
                .background(color = colors.surface, shape = HereShape.pill)
                .padding(
                    vertical = sizes.labelVerticalPadding,
                    horizontal = sizes.labelHorizontalPadding
                )
        )
    }
}
