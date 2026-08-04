package ru.aloyaloya.design_system.component.field

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Плашка даты или времени: подпись, текущее значение и иконка.
 *
 * Значение здесь не набирают с клавиатуры - плашка ведет себя как кнопка
 * и по нажатию открывает лист выбора.
 *
 * Ширину задает вызывающая сторона: в ряду дата обычно шире времени.
 *
 * @param label Подпись над значением. Регистр приводит сам компонент.
 * @param value Значение — дата или время в готовом для показа виде.
 * @param icon Иконка справа от значения.
 * @param onClick Колбэк нажатия.
 * @param modifier [Modifier], применяемый к плашке.
 */
@Composable
fun HereDateTimeField(
    label: String,
    value: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors
    val shape = MaterialTheme.shapes.medium

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(shape)
            .background(colors.surface)
            .border(HereSize.DateTimeField.border, colors.outlineStrong, shape)
            .clickable(onClick = onClick, role = Role.Button)
            .padding(
                horizontal = HereSize.DateTimeField.horizontalPadding,
                vertical = HereSize.DateTimeField.verticalPadding
            )
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(HereSize.DateTimeField.labelSpacing)) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = HereSize.DateTimeField.labelSize,
                    letterSpacing = HereSize.DateTimeField.labelLetterSpacing
                ),
                color = colors.textSecondary
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
                maxLines = 1
            )
        }

        Spacer(Modifier.weight(1f))

        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = colors.accent,
            modifier = Modifier.size(HereSize.DateTimeField.iconSize)
        )
    }
}