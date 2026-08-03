package ru.aloyaloya.design_system.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.extension.fabShadow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * FAB-кнопка приложения Here.
 *
 * Круглая кнопка акцентного цвета с иконкой «плюс». Лежит поверх карты,
 * поэтому вместо ripple у нее окрашенная тень.
 *
 * @param onClick Колбэк нажатия.
 * @param modifier [Modifier], применяемый к кнопке.
 */
@Composable
fun HereFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(HereSize.Fab.size)
            .fabShadow(HereShape.pill)
            .clip(HereShape.pill)
            .background(HereTheme.colors.accent)
            .clickable(onClick = onClick)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add),
            contentDescription = stringResource(R.string.fab_add_memory_content_description),
            tint = HereTheme.colors.onAccent,
            modifier = Modifier.size(HereSize.Fab.iconSize)
        )
    }
}
