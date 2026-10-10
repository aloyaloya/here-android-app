package ru.aloyaloya.design_system.component.topbar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import ru.aloyaloya.design_system.theme.HereMotion
import ru.aloyaloya.design_system.theme.HereTheme

private fun <T> enterSpec() = tween<T>(
    durationMillis = HereMotion.Duration.medium,
    delayMillis = HereMotion.Duration.short
)

private fun <T> exitSpec() = tween<T>(durationMillis = HereMotion.Duration.short)

/**
 * Секция заголовка для верхней панели приложения.
 *
 * Отображает только текст: иконка раздела убрана, текущий раздел и так виден
 * по нижней панели навигации. Заголовки сменяются сдвигом снизу вверх с проявлением.
 *
 * @param title Текст заголовка экрана.
 * @param modifier [Modifier], применяемый к тексту.
 */
@Composable
fun TopAppBarTitleSection(
    title: String,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = title,
        transitionSpec = {
            (slideInVertically(enterSpec()) { height -> height / 2 } +
                    fadeIn(enterSpec())) togetherWith
                    (slideOutVertically(exitSpec()) { height -> -height / 2 } +
                            fadeOut(exitSpec())) using
                    SizeTransform(clip = false)
        },
        contentAlignment = Alignment.CenterStart,
        label = "top-app-bar-title",
        modifier = modifier
    ) { shownTitle ->
        Text(
            text = shownTitle,
            color = HereTheme.colors.textPrimary,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}