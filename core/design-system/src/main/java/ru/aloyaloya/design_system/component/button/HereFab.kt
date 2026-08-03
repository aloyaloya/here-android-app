package ru.aloyaloya.design_system.component.button

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.aloyaloya.design_system.R
import ru.aloyaloya.design_system.extension.dropShadow
import ru.aloyaloya.design_system.theme.HereShapes

private val fabEmotions = listOf("😊", "🥰", "🤩", "😢")

private val buttonSize = 64.dp

/**
 * Расширяемая FAB-кнопка для приложения Here.
 *
 * При нажатии разворачивается влево, показывая список эмодзи для выбора эмоции.
 * Иконка «плюс» анимированно превращается в «крестик» при раскрытии.
 *
 * @param onEmotionSelected Колбэк, вызываемый при выборе эмодзи. Принимает символ эмодзи.
 * @param modifier [Modifier], применяемый к кнопке.
 */
@Composable
fun HereFab(
    onEmotionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val width by animateDpAsState(
        targetValue = if (expanded) buttonSize * (fabEmotions.size + 1) else buttonSize,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "fab-width"
    )

    val emojiAlpha by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fab-emoji-alpha"
    )

    val iconRotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fab-icon-rotation"
    )

    Box(
        modifier = modifier
            .height(buttonSize)
            .width(width)
            .dropShadow(HereShapes.extraLarge)
            .border(1.dp, MaterialTheme.colorScheme.secondaryContainer, HereShapes.extraLarge)
            .clip(HereShapes.extraLarge)
            .background(MaterialTheme.colorScheme.secondary, HereShapes.extraLarge)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .graphicsLayer { alpha = emojiAlpha }
        ) {
            fabEmotions.forEach { emoji ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(buttonSize)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = expanded
                        ) {
                            onEmotionSelected(emoji)
                            expanded = false
                        }
                ) {
                    Text(text = emoji, fontSize = 26.sp)
                }
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(buttonSize)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { expanded = !expanded }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier
                    .size(26.dp)
                    .rotate(iconRotation)
            )
        }
    }
}
