package ru.aloyaloya.design_system.component.topbar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Блок действий в правой части верхней панели.
 *
 * Пока в нём одна кнопка — переключатель светлой и тёмной темы.
 *
 * @param darkTheme Текущий режим темы: влияет на отображаемую иконку и анимацию.
 * @param onThemeChange Колбэк при нажатии на переключатель темы.
 * @param modifier [Modifier], применяемый к [Row].
 */
@Composable
fun TopAppBarActions(
    darkTheme: Boolean,
    onThemeChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        TopAppBarThemeToggle(
            darkTheme = darkTheme,
            onThemeChange = onThemeChange
        )
    }
}

/**
 * Круглая кнопка переключения темы.
 *
 * Показывает солнце или луну в зависимости от [darkTheme], смена иконки анимирована.
 * Ripple отключен: обратная связь — сама анимация иконки.
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
            .size(HereSize.TopAppBar.actionSize)
            .background(
                color = HereTheme.colors.surfaceMuted,
                shape = CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onThemeChange
            ),
        contentAlignment = Alignment.Center
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
                tint = HereTheme.colors.textPrimary,
                contentDescription = stringResource(R.string.theme_toggle_content_description),
                modifier = Modifier
                    .size(HereSize.TopAppBar.actionIconSize)
                    .rotate(iconRotation)
            )
        }
    }
}