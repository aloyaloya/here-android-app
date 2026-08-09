package ru.aloyaloya.design_system.component.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Подпись секции: короткий заголовок капсом над группой элементов.
 *
 * @param text Текст подписи. Регистр приводит сам компонент.
 * @param modifier [Modifier], применяемый к подписи.
 */
@Composable
fun HereSectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = HereTheme.colors.textSecondary,
        modifier = modifier
    )
}
