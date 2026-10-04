package ru.aloyaloya.design_system.component.picker

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ru.aloyaloya.design_system.extension.overlayShadow
import ru.aloyaloya.design_system.theme.HereSize
import ru.aloyaloya.design_system.theme.HereTheme

/** Прозрачность точки под прицелом. */
private const val ANCHOR_ALPHA = 0.35f

/**
 * Прицел режима выбора места.
 *
 * @param moving Едет ли сейчас камера.
 * @param modifier [Modifier], применяемый к прицелу.
 */
@Composable
fun PlacePin(
    moving: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = HereTheme.colors
    val sizes = HereSize.PlacePicker

    val lift by animateDpAsState(
        targetValue = if (moving) sizes.pinLift else 0.dp,
        label = "place-pin-lift"
    )

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
            .width(sizes.pinSize)
            .height(sizes.pinHeight)
    ) {
        Box(
            modifier = Modifier
                .size(sizes.pinAnchor)
                .background(color = colors.accent.copy(alpha = ANCHOR_ALPHA), shape = CircleShape)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = -lift)
        ) {
            Box(
                modifier = Modifier
                    .size(sizes.pinSize)
                    .overlayShadow(CircleShape)
                    .background(color = colors.accent, shape = CircleShape)
                    .border(width = sizes.pinBorder, color = colors.surface, shape = CircleShape)
            )

            Box(
                modifier = Modifier
                    .width(sizes.pinStemWidth)
                    .height(sizes.pinStemHeight)
                    .clip(CircleShape)
                    .background(colors.accent)
            )
        }
    }
}
