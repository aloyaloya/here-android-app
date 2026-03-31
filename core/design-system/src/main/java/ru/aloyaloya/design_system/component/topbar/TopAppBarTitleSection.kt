package ru.aloyaloya.design_system.component.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.theme.HereShapes

/**
 * Секция заголовка для верхней панели приложения.
 *
 * Отображает иконку раздела в контейнере с закруглением и текст заголовка рядом.
 * Стили и цвета берутся из текущей [androidx.compose.material3.MaterialTheme].
 *
 * @param painter [Painter] иконки текущего раздела.
 * @param title Текст заголовка экрана.
 * @param modifier [Modifier], применяемый к корневому [androidx.compose.foundation.layout.Row].
 */
@Composable
fun TopAppBarTitleSection(
    painter: Painter,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.small_spacer)
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = HereShapes.large
                )
                .padding(dimensionResource(R.dimen.topbar_item_padding))
        ) {
            Icon(
                painter = painter,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.large_icon_size))
            )
        }
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleLarge
        )
    }
}