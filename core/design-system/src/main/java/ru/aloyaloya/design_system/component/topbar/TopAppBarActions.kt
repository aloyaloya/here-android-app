package ru.aloyaloya.design_system.component.topbar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.extension.dropShadow

// TODO: add settings buttons

/**
 * Правая часть верхней панели с действиями (кнопки и переключатели).
 *
 * Содержит переключатель светлой и темной темы. Дополнительные кнопки
 * (например, настройки) можно добавить в тот же [androidx.compose.foundation.layout.Row].
 *
 * @param darkTheme Текущий режим темы: влияет на отображаемую иконку и анимацию.
 * @param onThemeChange Колбэк при нажатии на переключатель темы.
 * @param onOptionsNavigate Зарезервирован для перехода к экрану опций или настроек.
 * @param modifier [Modifier], применяемый к [androidx.compose.foundation.layout.Row].
 */
@Composable
fun TopAppBarActions(
    darkTheme: Boolean,
    onThemeChange: () -> Unit,
    onOptionsNavigate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        TopAppBarThemeToggle(
            darkTheme = darkTheme,
            onThemeChange = onThemeChange,
        )
    }
}

/**
 * Круглая кнопка переключения темы (светлая / темная) для [TopAppBar].
 *
 * Показывает солнце или луну в зависимости от [darkTheme], смена иконки анимирована,
 * нажатие без ripple. Тень и обводка согласованы с дизайн-системой Here.
 *
 * @param darkTheme Если `true`, отображается иконка солнца (переход к светлой теме).
 * @param onThemeChange Вызывается при нажатии на кнопку.
 * @param modifier [Modifier], применяемый к контейнеру кнопки.
 */
@Composable
private fun TopAppBarThemeToggle(
    darkTheme: Boolean,
    onThemeChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconRotation by animateFloatAsState(
        targetValue = if (darkTheme) 180f else 0f,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "theme_icon_rotation"
    )

    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = CircleShape
            )
            .dropShadow(CircleShape)
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onThemeChange
            )
            .padding(dimensionResource(R.dimen.topbar_item_padding))
    ) {
        AnimatedContent(
            targetState = if (darkTheme) R.drawable.ic_sun else R.drawable.ic_moon,
            transitionSpec = {
                fadeIn(animationSpec = tween(180)) togetherWith
                        fadeOut(animationSpec = tween(180))
            },
            label = "theme_icon_transition"
        ) { iconRes ->
            Icon(
                painter = painterResource(iconRes),
                tint = MaterialTheme.colorScheme.onTertiary,
                contentDescription = stringResource(R.string.theme_toggle_content_description),
                modifier = Modifier
                    .size(dimensionResource(R.dimen.large_icon_size))
                    .rotate(iconRotation)
            )
        }
    }
}