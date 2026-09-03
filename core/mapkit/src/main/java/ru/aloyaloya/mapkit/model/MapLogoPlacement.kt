package ru.aloyaloya.mapkit.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Угол карты, к которому прижат логотип Яндекса. */
enum class MapLogoCorner {
    TOP_START,
    TOP_CENTER
}

/**
 * Место логотипа Яндекса на карте.
 *
 * @param corner Угол, в котором стоит логотип.
 * @param horizontalInset Отступ от боковой границы карты.
 * @param verticalInset Отступ от верхней границы карты.
 */
data class MapLogoPlacement(
    val corner: MapLogoCorner,
    val horizontalInset: Dp,
    val verticalInset: Dp
) {
    companion object {
        val UnderTopBar = MapLogoPlacement(
            corner = MapLogoCorner.TOP_CENTER,
            horizontalInset = 0.dp,
            verticalInset = 86.dp
        )

        val Card = MapLogoPlacement(
            corner = MapLogoCorner.TOP_START,
            horizontalInset = 8.dp,
            verticalInset = 8.dp
        )
    }
}
