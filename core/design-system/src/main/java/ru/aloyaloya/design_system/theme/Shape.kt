package ru.aloyaloya.design_system.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Шкала скруглений приложения.
 */
object HereShape {
    val tile = RoundedCornerShape(16.dp)
    val card = RoundedCornerShape(24.dp)
    val sheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val pill = RoundedCornerShape(percent = 50)
}

val HereShapes = Shapes(
    extraSmall = HereShape.tile,
    small = HereShape.tile,
    medium = HereShape.tile,
    large = HereShape.card,
    extraLarge = HereShape.card
)
