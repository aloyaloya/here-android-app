package ru.aloyaloya.design_system.component.field

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import ru.aloyaloya.design_system.extension.cardShadow
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/**
 * Поле ввода приложения Here — карточка на поверхности, без рамки и подчеркивания.
 *
 * Высоту поле берет по содержимому, но не меньше [minHeight]: так однострочный
 * заголовок и многострочное описание собираются из одного компонента.
 *
 * @param value Текущий текст.
 * @param onValueChange Колбэк изменения текста.
 * @param placeholder Подсказка, видна пока поле пустое.
 * @param modifier [Modifier], применяемый к полю.
 * @param textStyle Стиль текста и подсказки.
 * @param singleLine Запрещать ли переносы строк.
 * @param minHeight Минимальная высота карточки без учета отступов.
 */
@Composable
fun HereTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    singleLine: Boolean = false,
    minHeight: Dp = Dp.Unspecified
) {
    val colors = HereTheme.colors

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle.copy(color = colors.textPrimary),
        cursorBrush = SolidColor(colors.accent),
        singleLine = singleLine,
        modifier = modifier
            .fillMaxWidth()
            .cardShadow(HereShape.tile)
            .clip(HereShape.tile)
            .background(colors.surface)
            .padding(
                vertical = HereSize.TextField.verticalPadding,
                horizontal = HereSize.TextField.horizontalPadding
            )
            .defaultMinSize(minHeight = minHeight)
    ) { innerTextField ->
        Box {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = textStyle,
                    color = colors.textTertiary
                )
            }
            innerTextField()
        }
    }
}
