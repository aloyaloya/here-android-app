package ru.aloyaloya.design_system.component.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Плашка с путем в системных настройках: где включить запрещенное разрешение.
 *
 * @param label Подпись над путем.
 * @param path Путь: «Разрешения › Местоположение».
 * @param modifier [Modifier], применяемый к плашке.
 */
@Composable
fun SettingsPathCard(
    label: String,
    path: String,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors
    val sizes = HereSize.SettingsPathCard

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(sizes.spacing),
        modifier = modifier
            .fillMaxWidth()
            .background(color = colors.surfaceMuted, shape = HereShape.tile)
            .padding(vertical = sizes.verticalPadding, horizontal = sizes.horizontalPadding)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_settings),
            contentDescription = null,
            tint = colors.textPrimary,
            modifier = Modifier.size(sizes.iconSize)
        )

        Column(verticalArrangement = Arrangement.spacedBy(sizes.textSpacing)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = sizes.labelSize),
                color = colors.textBody
            )

            Text(
                text = path,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = sizes.pathSize,
                    fontWeight = FontWeight.SemiBold
                ),
                color = colors.textPrimary
            )
        }
    }
}
