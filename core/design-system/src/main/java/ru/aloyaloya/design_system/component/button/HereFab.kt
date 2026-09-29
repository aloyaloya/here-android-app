package ru.aloyaloya.design_system.component.button

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.extension.fabShadow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/** Длительность превращения иконки в подпись и обратно. */
private const val MORPH_MILLIS = 260

/**
 * FAB-кнопка приложения Here.
 *
 * Квадрат со скругленными углами — форма FAB в Material 3. Лежит поверх карты,
 * поэтому вместо ripple у нее окрашенная тень.
 *
 * С [text] кнопка становится расширенной. Это не вторая кнопка, а то же самое
 * действие, которому потребовалось имя, поэтому подпись не подменяет иконку, а
 * растягивает кнопку: нажатое остается на месте, меняется только его ширина.
 *
 * @param onClick Колбэк нажатия.
 * @param modifier [Modifier], применяемый к кнопке.
 * @param text Подпись вместо иконки или null, если кнопка обходится иконкой.
 * @param icon Иконка кнопки.
 * @param contentDescription Описание действия для программ чтения с экрана.
 * @param container Цвет кнопки.
 * @param content Цвет иконки и подписи.
 */
@Composable
fun HereFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    @DrawableRes icon: Int = R.drawable.ic_add,
    contentDescription: String? = stringResource(R.string.fab_add_memory_content_description),
    container: Color = HereTheme.colors.accent,
    content: Color = HereTheme.colors.onAccent
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(HereSize.Fab.size)
            .fabShadow(HereShape.tile)
            .clip(HereShape.tile)
            .background(container)
            .clickable(onClick = onClick)
    ) {
        AnimatedContent(
            targetState = text,
            transitionSpec = {
                fadeIn(tween(MORPH_MILLIS, delayMillis = MORPH_MILLIS / 2)) togetherWith
                    fadeOut(tween(MORPH_MILLIS / 2)) using
                    SizeTransform { _, _ -> tween(MORPH_MILLIS) }
            },
            label = "fab-content"
        ) { label ->
            if (label == null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.width(HereSize.Fab.size)
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = contentDescription,
                        tint = content,
                        modifier = Modifier.size(HereSize.Fab.iconSize)
                    )
                }
            } else {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = content,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = HereSize.Fab.extendedPadding)
                )
            }
        }
    }
}
