package ru.aloyaloya.mapkit.ui

import android.content.res.Resources
import androidx.compose.ui.unit.Dp
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.logo.Padding
import com.yandex.mapkit.mapview.MapView
import ru.aloyaloya.mapkit.model.MapLogoCorner
import ru.aloyaloya.mapkit.model.MapLogoPlacement

/**
 * Ставит логотип Яндекса туда, где он не мешает содержимому экрана.
 *
 * Отступы MapKit задаются в пикселях, поэтому dp переводятся вручную.
 *
 * @param placement Угол и отступы логотипа.
 */
fun MapView.applyHereLogoPlacement(placement: MapLogoPlacement) {
    mapWindow.map.logo.apply {
        setAlignment(
            Alignment(placement.corner.toHorizontalAlignment(), VerticalAlignment.TOP)
        )
        setPadding(
            Padding(placement.horizontalInset.toPx(), placement.verticalInset.toPx())
        )
    }
}

private fun MapLogoCorner.toHorizontalAlignment(): HorizontalAlignment = when (this) {
    MapLogoCorner.TOP_START -> HorizontalAlignment.LEFT
    MapLogoCorner.TOP_CENTER -> HorizontalAlignment.CENTER
}

private fun Dp.toPx(): Int = (value * Resources.getSystem().displayMetrics.density).toInt()
