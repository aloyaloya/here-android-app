package ru.aloyaloya.design_system.component.topbar

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Секция заголовка для верхней панели приложения.
 *
 * Отображает только текст: иконка раздела убрана, текущий раздел и так виден
 * по нижней панели навигации.
 *
 * @param title Текст заголовка экрана.
 * @param modifier [Modifier], применяемый к тексту.
 */
@Composable
fun TopAppBarTitleSection(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        modifier = modifier,
        color = HereTheme.colors.textPrimary,
        style = MaterialTheme.typography.titleLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}