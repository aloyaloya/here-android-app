package ru.aloyaloya.design_system.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val HereShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

/**
 * Формы, которых нет в шкале [HereShapes]: скругления карточек, листов,
 * нижней панели навигации и кнопок-пилюль.
 */
object HereShape {
    val chip = RoundedCornerShape(14.dp)
    val tile = RoundedCornerShape(18.dp)
    val card = RoundedCornerShape(22.dp)
    val cardLarge = RoundedCornerShape(26.dp)
    val pill = RoundedCornerShape(percent = 50)
    val navBar = RoundedCornerShape(41.dp)
    val sheet = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    val sheetDetail = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
}