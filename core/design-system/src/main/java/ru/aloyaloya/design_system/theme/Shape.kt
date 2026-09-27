package ru.aloyaloya.design_system.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Шкала скруглений приложения — ступени Material 3.
 */
object HereShape {
    /** Карточки и группы внутри экрана. */
    val card = RoundedCornerShape(12.dp)

    /** Плитки, поля, чипы и FAB. */
    val tile = RoundedCornerShape(16.dp)

    /** Диалоги. */
    val dialog = RoundedCornerShape(28.dp)

    val sheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val pill = RoundedCornerShape(percent = 50)
}

val HereShapes = Shapes(
    extraSmall = HereShape.card,
    small = HereShape.card,
    medium = HereShape.card,
    large = HereShape.tile,
    extraLarge = HereShape.dialog
)
