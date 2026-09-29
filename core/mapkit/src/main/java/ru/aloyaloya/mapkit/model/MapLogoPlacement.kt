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
        private val TopBarHeight = 64.dp
        private val TopBarGap = 22.dp

        /** Карта уходит под верхнюю панель: логотип опущен на ее высоту. */
        val UnderTopBar = MapLogoPlacement(
            corner = MapLogoCorner.TOP_CENTER,
            horizontalInset = 0.dp,
            verticalInset = TopBarHeight + TopBarGap
        )

        /** Карта начинается сразу под верхней панелью. */
        val BelowTopBar = MapLogoPlacement(
            corner = MapLogoCorner.TOP_CENTER,
            horizontalInset = 0.dp,
            verticalInset = TopBarGap
        )

        val Card = MapLogoPlacement(
            corner = MapLogoCorner.TOP_START,
            horizontalInset = 8.dp,
            verticalInset = 8.dp
        )
    }
}
