package ru.aloyaloya.design_system.component.field

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.takeOrElse
import ru.aloyaloya.design_system.theme.HereShape
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/** Длительность всплытия подписи и появления обводки. */
private const val LABEL_MILLIS = 180

/**
 * Поле ввода приложения Here — заливка приглушенным фоном, без рамки и подчеркивания.
 *
 * Высоту поле берет по содержимому, но не меньше [minHeight]: так однострочный
 * заголовок и многострочное описание собираются из одного компонента.
 *
 * Подпись работает как в Material: пока поле пустое и не в фокусе, она лежит на
 * месте первой строки и заменяет подсказку, а с приходом текста мельчает и всплывает
 * наверх. Так поле не теряет имя, когда заполнено. Фокус Material показывает линией
 * снизу, но она требует прямого низа у контейнера, поэтому здесь его показывает
 * обводка по контуру.
 *
 * @param value Текущий текст.
 * @param onValueChange Колбэк изменения текста.
 * @param label Имя поля: и подсказка в пустом поле, и подпись в заполненном.
 * @param modifier [Modifier], применяемый к полю.
 * @param textStyle Стиль текста.
 * @param singleLine Запрещать ли переносы строк. В однострочном поле переносить нечего,
 * поэтому клавиатура вместо Enter предлагает закончить ввод.
 * @param minHeight Минимальная высота карточки без учета отступов.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HereTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    singleLine: Boolean = false,
    minHeight: Dp = Dp.Unspecified
) {
    val colors = HereTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val floating = focused || value.isNotEmpty()

    val progress by animateFloatAsState(
        targetValue = if (floating) 1f else 0f,
        animationSpec = tween(LABEL_MILLIS),
        label = "text-field-label"
    )

    val labelColor by animateColorAsState(
        targetValue = when {
            focused -> colors.accent
            floating -> colors.textSecondary
            else -> colors.textTertiary
        },
        animationSpec = tween(LABEL_MILLIS),
        label = "text-field-label-color"
    )

    val borderColor by animateColorAsState(
        targetValue = if (focused) colors.accent else Color.Transparent,
        animationSpec = tween(LABEL_MILLIS),
        label = "text-field-border"
    )

    /** Опущенная подпись должна лечь ровно на первую строку текста, то есть под свой слот. */
    val restingOffset = with(LocalDensity.current) { HereSize.TextField.labelSlot.toPx() }
    val restingSize = textStyle.fontSize.takeOrElse { MaterialTheme.typography.bodyLarge.fontSize }

    val focusManager = LocalFocusManager.current

    val imeVisible = WindowInsets.isImeVisible
    LaunchedEffect(imeVisible) {
        if (!imeVisible && focused) focusManager.clearFocus()
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle.copy(color = colors.textPrimary),
        cursorBrush = SolidColor(colors.accent),
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(
            imeAction = if (singleLine) ImeAction.Done else ImeAction.Default
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .clip(HereShape.tile)
            .background(colors.surfaceMuted)
            .border(HereSize.TextField.focusBorder, borderColor, HereShape.tile)
            .padding(
                vertical = HereSize.TextField.verticalPadding,
                horizontal = HereSize.TextField.horizontalPadding
            )
            .defaultMinSize(minHeight = minHeight)
    ) { innerTextField ->
        Column(
            modifier = Modifier.graphicsLayer {
                translationY = -(1f - progress) * restingOffset / 2f
            }
        ) {
            Box(modifier = Modifier.height(HereSize.TextField.labelSlot)) {
                Text(
                    text = label,
                    style = textStyle.copy(
                        fontSize = lerp(restingSize, HereSize.TextField.labelSize, progress)
                    ),
                    color = labelColor,
                    modifier = Modifier
                        .wrapContentHeight(align = Alignment.Top, unbounded = true)
                        .graphicsLayer {
                            translationY = (1f - progress) * restingOffset
                        }
                )
            }

            innerTextField()
        }
    }
}
